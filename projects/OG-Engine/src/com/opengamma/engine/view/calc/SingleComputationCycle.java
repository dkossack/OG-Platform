/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.depgraph.DependencyGraph;
import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.depgraph.DependencyNodeFilter;
import com.opengamma.engine.function.LiveDataSourcingFunction;
import com.opengamma.engine.livedata.LiveDataSnapshotProvider;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.engine.view.InMemoryViewComputationResultModel;
import com.opengamma.engine.view.ViewComputationResultModel;
import com.opengamma.engine.view.ViewDefinition;
import com.opengamma.engine.view.ViewInternal;
import com.opengamma.engine.view.ViewProcessingContext;
import com.opengamma.engine.view.cache.CacheSelectHint;
import com.opengamma.engine.view.cache.ViewComputationCache;
import com.opengamma.engine.view.calc.stats.GraphExecutorStatisticsGatherer;
import com.opengamma.engine.view.compilation.ViewEvaluationModel;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.Pair;

/**
 * Holds all data and actions for a single pass through a computation cycle.
 * In general, each invocation of {@link ViewRecalculationJob#runOneCycle()}
 * will create an instance of this class.
 * <p/>
 * At the moment, the concurrency metaphor is:
 * <ul>
 *   <li>Each distinct security has its own execution plan</li>
 *   <li>The cycle will schedule each node in the execution plan sequentially</li>
 *   <li>If there are shared sub-graphs that aren't security specific, they'll be captured at execution time.</li>
 * </ul>
 * This is, of course, not optimal, and later on we can fix that.
 *
 * @author kirk
 */
public class SingleComputationCycle {
  private static final Logger s_logger = LoggerFactory.getLogger(SingleComputationCycle.class);
  // Injected Inputs:
  private final ViewInternal _view;
  private final ViewEvaluationModel _viewEvaluationModel;
  private final LiveDataSnapshotProvider _snapshotProvider;
  private final Instant _valuationTime;

  private final DependencyGraphExecutor<?> _dependencyGraphExecutor;
  private final GraphExecutorStatisticsGatherer _statisticsGatherer;

  // State:

  /** Current state of the cycle */
  private enum State {
    CREATED, INPUTS_PREPARED, EXECUTING, EXECUTION_INTERRUPTED, FINISHED, CLEANED
  }

  private State _state;

  /**
   * Nanoseconds, see System.nanoTime()
   */
  private long _startTime;

  /**
   * Nanoseconds, see System.nanoTime()
   */
  private long _endTime;

  private final ReentrantReadWriteLock _nodeExecutionLock = new ReentrantReadWriteLock();
  private final Set<DependencyNode> _executedNodes = new HashSet<DependencyNode>();
  private final Set<DependencyNode> _failedNodes = new HashSet<DependencyNode>();
  private final Map<String, ViewComputationCache> _cachesByCalculationConfiguration = new HashMap<String, ViewComputationCache>();

  // Outputs:
  private final InMemoryViewComputationResultModel _resultModel;

  public SingleComputationCycle(ViewInternal view,
      ViewEvaluationModel viewEvaluationModel,
      LiveDataSnapshotProvider snapshotProvider,
      long valuationTime) {
    ArgumentChecker.notNull(view, "view");
    ArgumentChecker.notNull(viewEvaluationModel, "viewEvaluationModel");
    ArgumentChecker.notNull(snapshotProvider, "snapshotProvider");

    _view = view;
    _viewEvaluationModel = viewEvaluationModel;
    _snapshotProvider = snapshotProvider;
    
    _valuationTime = Instant.ofEpochMillis(valuationTime);

    _resultModel = new InMemoryViewComputationResultModel();
    _resultModel.setCalculationConfigurationNames(getViewEvaluationModel().getDependencyGraphsByConfiguration().keySet());

    if (getViewEvaluationModel().getPortfolio() != null) {
      _resultModel.setPortfolio(getViewEvaluationModel().getPortfolio());
    }

    _dependencyGraphExecutor = getProcessingContext().getDependencyGraphExecutorFactory().createExecutor(this);
    _statisticsGatherer = getProcessingContext().getGraphExecutorStatisticsGathererProvider().getStatisticsGatherer(view);

    _state = State.CREATED;
  }

  public ViewInternal getView() {
    return _view;
  }
  
  public LiveDataSnapshotProvider getSnapshotProvider() {
    return _snapshotProvider;
  }

  public Instant getValuationTime() {
    return _valuationTime;
  }

  public long getFunctionInitId() {
    return getViewEvaluationModel().getFunctionInitId();
  }

  /**
   * @return the viewName
   */
  public String getViewName() {
    return getView().getName();
  }

  /**
   * @return the processingContext
   */
  public ViewProcessingContext getProcessingContext() {
    return getView().getProcessingContext();
  }

  /**
   * @return the start time. Nanoseconds, see {@link System#nanoTime()}. 
   */
  public long getStartTime() {
    return _startTime;
  }

  /**
   * @return the end time. Nanoseconds, see {@link System#nanoTime()}. 
   */
  public long getEndTime() {
    return _endTime;
  }

  /**
   * @return How many nanoseconds the cycle took
   */
  public long getDurationNanos() {
    return getEndTime() - getStartTime();
  }

  /**
   * @return the resultModel
   */
  public InMemoryViewComputationResultModel getResultModel() {
    return _resultModel;
  }

  public ViewComputationCache getComputationCache(String calcConfigName) {
    return _cachesByCalculationConfiguration.get(calcConfigName);
  }

  /**
   * @return the viewDefinition
   */
  public ViewDefinition getViewDefinition() {
    return getView().getDefinition();
  }

  public DependencyGraphExecutor<?> getDependencyGraphExecutor() {
    return _dependencyGraphExecutor;
  }

  public GraphExecutorStatisticsGatherer getStatisticsGatherer() {
    return _statisticsGatherer;
  }

  public Map<String, ViewComputationCache> getCachesByCalculationConfiguration() {
    return Collections.unmodifiableMap(_cachesByCalculationConfiguration);
  }

  public ViewEvaluationModel getViewEvaluationModel() {
    return _viewEvaluationModel;
  }

  public Set<String> getAllCalculationConfigurationNames() {
    return getViewEvaluationModel().getDependencyGraphsByConfiguration().keySet();
  }

  // --------------------------------------------------------------------------
  
  public void execute() {
    prepareInputs();
    try {
      executePlans();
    } catch (InterruptedException e) {
      s_logger.warn("Interrupted while attempting to run a single computation cycle. No results will be output.");
    }
    releaseResources();
  }
  
  public ViewComputationResultModel executeWithResult() {
    prepareInputs();
    try {
      executePlans();
    } catch (InterruptedException e) {
      s_logger.warn("Interrupted while attempting to run a single computation cycle. No results will be output.");
      releaseResources();
      return null;
    }

    populateResultModel();
    releaseResources();
    
    return getResultModel();
  }
  
  // --------------------------------------------------------------------------
  
  public void prepareInputs() {
    if (_state != State.CREATED) {
      throw new IllegalStateException("State must be " + State.CREATED);
    }

    _startTime = System.nanoTime();

    getResultModel().setViewName(getViewName());
    getResultModel().setValuationTime(getValuationTime());

    createAllCaches();

    Map<ValueRequirement, ValueSpecification> allLiveDataRequirements = getViewEvaluationModel().getAllLiveDataRequirements();
    s_logger.debug("Populating {} market data items for snapshot {}", allLiveDataRequirements.size(), getValuationTime());
    
    getSnapshotProvider().snapshot(getValuationTime().toEpochMillisLong());

    Set<ValueSpecification> missingLiveData = new HashSet<ValueSpecification>();
    for (Map.Entry<ValueRequirement, ValueSpecification> liveDataRequirement : allLiveDataRequirements.entrySet()) {
      // REVIEW 2010-10-22 Andrew
      // If we're asking the snapshot for a "requirement" then it should give back a more detailed "specification" with the data (i.e. a
      // ComputedValue instance where the specification satisfies the requirement. Functions should then declare their requirements and
      // not the exact specification they want for live data. Alternatively, if the snapshot will give us the exact value we ask for then
      // we should be querying with a "specification" and not a requirement.
      Object data = getSnapshotProvider().querySnapshot(getValuationTime().toEpochMillisLong(), liveDataRequirement.getKey());
      if (data == null) {
        s_logger.debug("Unable to load live data value for {} at snapshot {}.", liveDataRequirement, getValuationTime());
        missingLiveData.add(liveDataRequirement.getValue());
      } else {
        ComputedValue dataAsValue = new ComputedValue(liveDataRequirement.getValue(), data);
        addToAllCaches(dataAsValue);
        getResultModel().addLiveData(dataAsValue);
      }
    }
    if (!missingLiveData.isEmpty()) {
      s_logger.warn("Missing {} live data elements: {}", missingLiveData.size(), formatMissingLiveData(missingLiveData));
    }
    

    _state = State.INPUTS_PREPARED;
  }

  protected static String formatMissingLiveData(Set<ValueSpecification> missingLiveData) {
    StringBuilder sb = new StringBuilder();
    for (ValueSpecification spec : missingLiveData) {
      sb.append("[").append(spec.getValueName()).append(" on ");
      sb.append(spec.getTargetSpecification().getType());
      if (spec.getTargetSpecification().getType() == ComputationTargetType.PRIMITIVE) {
        sb.append("-").append(spec.getTargetSpecification().getIdentifier().getScheme().getName());
      }
      sb.append(":").append(spec.getTargetSpecification().getIdentifier().getValue()).append("] ");
    }
    return sb.toString();
  }
  
  /**
   * 
   */
  private void createAllCaches() {
    for (String calcConfigurationName : getAllCalculationConfigurationNames()) {
      ViewComputationCache cache = getProcessingContext().getComputationCacheSource().getCache(getViewName(), calcConfigurationName, getValuationTime().toEpochMillisLong());
      _cachesByCalculationConfiguration.put(calcConfigurationName, cache);
    }
  }

  /**
   * @param dataAsValue
   */
  private void addToAllCaches(ComputedValue dataAsValue) {
    for (String calcConfigurationName : getAllCalculationConfigurationNames()) {
      getComputationCache(calcConfigurationName).putSharedValue(dataAsValue);
    }
  }

  // --------------------------------------------------------------------------

  /**
   * Determine which live data inputs have changed between iterations, and:
   * <ul>
   * <li>Copy over all values that can be demonstrated to be the same from the previous iteration (because no input has changed)
   * <li>Only recompute the values that could have changed based on live data inputs
   * </ul> 
   * 
   * @param previousCycle Previous iteration. It must not have been cleaned yet ({@link #releaseResources()}).
   */
  public void computeDelta(SingleComputationCycle previousCycle) {
    if (_state != State.INPUTS_PREPARED) {
      throw new IllegalStateException("State must be " + State.INPUTS_PREPARED);
    }
    if (previousCycle._state != State.FINISHED) {
      throw new IllegalArgumentException("State of previous cycle must be " + State.FINISHED);
    }

    for (String calcConfigurationName : getAllCalculationConfigurationNames()) {
      DependencyGraph depGraph = getViewEvaluationModel().getDependencyGraph(calcConfigurationName);

      ViewComputationCache cache = getComputationCache(calcConfigurationName);
      ViewComputationCache previousCache = previousCycle.getComputationCache(calcConfigurationName);

      LiveDataDeltaCalculator deltaCalculator = new LiveDataDeltaCalculator(depGraph, cache, previousCache);
      deltaCalculator.computeDelta();

      s_logger.info("Computed delta for calc conf {}. Of {} nodes, {} require recomputation.", new Object[] {calcConfigurationName, depGraph.getSize(), deltaCalculator.getChangedNodes().size()});

      for (DependencyNode unchangedNode : deltaCalculator.getUnchangedNodes()) {
        markExecuted(unchangedNode);

        for (ValueSpecification spec : unchangedNode.getOutputValues()) {
          Object previousValue = previousCache.getValue(spec);
          if (previousValue != null) {
            cache.putSharedValue(new ComputedValue(spec, previousValue));
          }
        }
      }
    }
  }

  // REVIEW kirk 2009-11-03 -- This is a database kernel. Act accordingly.
  /**
   * Synchronously runs the computation cycle.
   * 
   * @throws InterruptedException  if the thread is interrupted while waiting for the computation cycle to complete.
   *                               Execution of any outstanding jobs will be cancelled, but {@link #releaseResources()}
   *                               still must be called. 
   */
  public void executePlans() throws InterruptedException {
    if (_state != State.INPUTS_PREPARED) {
      throw new IllegalStateException("State must be " + State.INPUTS_PREPARED);
    }
    _state = State.EXECUTING;

    LinkedList<Future<?>> futures = new LinkedList<Future<?>>();

    for (String calcConfigurationName : getAllCalculationConfigurationNames()) {
      s_logger.info("Executing plans for calculation configuration {}", calcConfigurationName);
      DependencyGraph depGraph = getExecutableDependencyGraph(calcConfigurationName);

      s_logger.info("Submitting {} for execution by {}", depGraph, getDependencyGraphExecutor());

      Future<?> future = getDependencyGraphExecutor().execute(depGraph, _statisticsGatherer);
      futures.add(future);
    }

    while (!futures.isEmpty()) {
      Future<?> future = futures.poll();
      try {
        future.get(5, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        s_logger.info("Waiting for " + future);
        futures.add(future);
      } catch (InterruptedException e) {
        Thread.interrupted();
        // Cancel all outstanding jobs to free up resources
        future.cancel(true);
        for (Future<?> incompleteFuture : futures) {
          incompleteFuture.cancel(true);
        }
        _state = State.EXECUTION_INTERRUPTED;
        s_logger.info("Execution interrupted before completion.");
        throw e;
      } catch (ExecutionException e) {
        s_logger.error("Unable to execute dependency graph", e);
        // Should we be swallowing this or not?
        throw new OpenGammaRuntimeException("Unable to execute dependency graph", e);
      }
    }

    _state = State.FINISHED;
  }

  private DependencyGraph getDependencyGraph(String calcConfName) {
    DependencyGraph depGraph = getViewEvaluationModel().getDependencyGraph(calcConfName);
    return depGraph;
  }

  /**
   * @param calcConfName configuration name
   * @return A dependency graph with nodes already executed stripped out.
   * See {@link #computeDelta} and how it calls {@link #markExecuted}.
   */
  protected DependencyGraph getExecutableDependencyGraph(String calcConfName) {
    DependencyGraph originalDepGraph = getDependencyGraph(calcConfName);

    DependencyGraph dependencyGraph = originalDepGraph.subGraph(new DependencyNodeFilter() {
      public boolean accept(DependencyNode node) {
        // LiveData functions do not need to be computed.
        if (node.getFunction().getFunction() instanceof LiveDataSourcingFunction) {
          markExecuted(node);
        }

        return !isExecuted(node);
      }
    });
    return dependencyGraph;
  }

  // --------------------------------------------------------------------------

  public void populateResultModel() {
    Instant resultTimestamp = Instant.now();
    getResultModel().setResultTimestamp(resultTimestamp);

    for (String calcConfigurationName : getAllCalculationConfigurationNames()) {
      DependencyGraph depGraph = getViewEvaluationModel().getDependencyGraph(calcConfigurationName);
      populateResultModel(calcConfigurationName, depGraph);
    }

    _endTime = System.nanoTime();
  }

  protected void populateResultModel(String calcConfigurationName, DependencyGraph depGraph) {
    ViewComputationCache computationCache = getComputationCache(calcConfigurationName);
    for (Pair<ValueSpecification, Object> value : computationCache.getValues(depGraph.getOutputValues(), CacheSelectHint.allShared())) {
      if (value.getValue() == null) {
        continue;
      }
      if (!getViewDefinition().getResultModelDefinition().shouldOutputResult(value.getFirst(), depGraph)) {
        continue;
      }
      getResultModel().addValue(calcConfigurationName, new ComputedValue(value.getFirst(), value.getSecond()));
    }
  }

  public void releaseResources() {
    if (_state != State.FINISHED && _state != State.EXECUTION_INTERRUPTED) {
      throw new IllegalStateException("State must be " + State.FINISHED + " or " + State.EXECUTION_INTERRUPTED);
    }

    if (getViewDefinition().isDumpComputationCacheToDisk()) {
      dumpComputationCachesToDisk();
    }

    getSnapshotProvider().releaseSnapshot(getValuationTime().toEpochMillisLong()); // BUG - what if 2 cycles use the same snapshot provider with the same valuation time?
    getProcessingContext().getComputationCacheSource().releaseCaches(getViewName(), getValuationTime().toEpochMillisLong());

    _state = State.CLEANED;
  }

  public void dumpComputationCachesToDisk() {
    for (String calcConfigurationName : getAllCalculationConfigurationNames()) {
      DependencyGraph depGraph = getDependencyGraph(calcConfigurationName);
      ViewComputationCache computationCache = getComputationCache(calcConfigurationName);

      TreeMap<String, Object> key2Value = new TreeMap<String, Object>();
      for (ValueSpecification outputSpec : depGraph.getOutputValues()) {
        Object value = computationCache.getValue(outputSpec);
        key2Value.put(outputSpec.toString(), value);
      }

      try {
        File file = File.createTempFile("computation-cache-" + calcConfigurationName + "-", ".txt");
        s_logger.info("Dumping cache for calc conf " + calcConfigurationName + " to " + file.getAbsolutePath());
        FileWriter writer = new FileWriter(file);
        writer.write(key2Value.toString());
        writer.close();
      } catch (IOException e) {
        throw new RuntimeException("Writing cache to file failed", e);
      }
    }
  }

  // --------------------------------------------------------------------------

  public boolean isExecuted(DependencyNode node) {
    if (node == null) {
      return true;
    }
    _nodeExecutionLock.readLock().lock();
    try {
      return _executedNodes.contains(node);
    } finally {
      _nodeExecutionLock.readLock().unlock();
    }
  }

  public void markExecuted(DependencyNode node) {
    if (node == null) {
      return;
    }
    _nodeExecutionLock.writeLock().lock();
    try {
      _executedNodes.add(node);
    } finally {
      _nodeExecutionLock.writeLock().unlock();
    }
  }

  public boolean isFailed(DependencyNode node) {
    if (node == null) {
      return true;
    }
    _nodeExecutionLock.readLock().lock();
    try {
      return _failedNodes.contains(node);
    } finally {
      _nodeExecutionLock.readLock().unlock();
    }
  }

  public void markFailed(DependencyNode node) {
    if (node == null) {
      return;
    }
    _nodeExecutionLock.writeLock().lock();
    try {
      _failedNodes.add(node);
    } finally {
      _nodeExecutionLock.writeLock().unlock();
    }
  }
}
