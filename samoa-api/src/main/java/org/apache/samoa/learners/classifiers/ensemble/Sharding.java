//package org.apache.samoa.learners.classifiers.ensemble;
//
///*
// * #%L
// * SAMOA
// * %%
// * Copyright (C) 2014 - 2015 Apache Software Foundation
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import java.util.Set;
//
//import com.github.javacliparser.ClassOption;
//import com.github.javacliparser.Configurable;
//import com.github.javacliparser.IntOption;
//import com.google.common.collect.ImmutableSet;
//import com.yahoo.labs.samoa.core.Processor;
//import com.yahoo.labs.samoa.instances.Instances;
//import com.yahoo.labs.samoa.learners.Learner;
//import com.yahoo.labs.samoa.learners.classifiers.trees.VerticalHoeffdingTree;
//import com.yahoo.labs.samoa.topology.Stream;
//import com.yahoo.labs.samoa.topology.TopologyBuilder;
//
///**
// * Simple sharding meta-classifier. It trains an ensemble of learners by shuffling the training stream among them, so
// * that each learner is completely independent from each other.
// */
//public class Sharding implements Learner, Configurable {
//
//  private static final long serialVersionUID = -2971850264864952099L;
//
//  /** The base learner class. */
//  public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l',
//      "Classifier to train.", Learner.class, VerticalHoeffdingTree.class.getName());
//
//  /** The ensemble size option. */
//  public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's',
//      "The number of models in the bag.", 10, 1, Integer.MAX_VALUE);
//
//  /** The distributor processor. */
//  private ShardingDistributorProcessor distributor;
//
//  /** The training stream. */
//  private Stream testingStream;
//
//  /** The prediction stream. */
//  private Stream predictionStream;
//
//  /** The result stream. */
//  protected Stream resultStream;
//
//  /** The dataset. */
//  private Instances dataset;
//
//  protected Learner classifier;
//
//  protected int parallelism;
//
//  /**
//   * Sets the layout.
//   */
//  protected void setLayout() {
//
//    int ensembleSize = this.ensembleSizeOption.getValue();
//
//    distributor = new ShardingDistributorProcessor();
//    distributor.setEnsembleSize(ensembleSize);
//    this.builder.addProcessor(distributor, 1);
//
//    // instantiate classifier
//    classifier = (Learner) this.baseLearnerOption.getValue();
//    classifier.init(builder, this.dataset, ensembleSize);
//
//    PredictionCombinerProcessor predictionCombiner = new PredictionCombinerProcessor();
//    predictionCombiner.setEnsembleSize(ensembleSize);
//    this.builder.addProcessor(predictionCombiner, 1);
//
//    // Streams
//    resultStream = this.builder.createStream(predictionCombiner);
//    predictionCombiner.setOutputStream(resultStream);
//
//    for (Stream subResultStream : classifier.getResultStreams()) {
//      this.builder.connectInputKeyStream(subResultStream, predictionCombiner);
//    }
//
//    testingStream = this.builder.createStream(distributor);
//    this.builder.connectInputKeyStream(testingStream, classifier.getInputProcessor());
//
//    predictionStream = this.builder.createStream(distributor);
//    this.builder.connectInputKeyStream(predictionStream, classifier.getInputProcessor());
//
//    distributor.setOutputStream(testingStream);
//    distributor.setPredictionStream(predictionStream);
//  }
//
//  /** The builder. */
//  private TopologyBuilder builder;
//
//  @Override
//  public void init(TopologyBuilder builder, Instances dataset, int parallelism) {
//    this.builder = builder;
//    this.dataset = dataset;
//    this.parallelism = parallelism;
//    this.setLayout();
//  }
//
//  @Override
//  public Processor getInputProcessor() {
//    return distributor;
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see samoa.learners.Learner#getResultStreams()
//   */
//  @Override
//  public Set<Stream> getResultStreams() {
//    return ImmutableSet.of(this.resultStream);
//  }
//}
