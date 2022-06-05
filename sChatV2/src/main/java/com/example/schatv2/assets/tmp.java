//package com.example.schatv2.assets;
//
//import javafx.concurrent.Task;
//
//public class tmp {
//}
//
//public class KnuthMorrisPratt {
//    public Task<Void> runFactory() throws InterruptedException {
//        return new Task<Void>() {
//
//            @Override public Void call() throws InterruptedException {
//                for (InputSequence seq: getSequences) {
//                    KnuthMorrisPratt kmp = new KnuthMorrisPratt(seq, substring);
//                    kmp.align();
//
//                }
//                return null;
//            }
//        };
//    }
//
//    // High-level class to run the Knuth-Morris-Pratt algorithm.
//    public class AlignmentFactory {
//        public void perform() {
//            KnuthMorrisPrattFactory factory = new KnuthMorrisPrattFactory();
//            Task<Void> runFactoryTask = factory.runFactory();
//            runFactoryTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//                @Override
//                public void handle(WorkerStateEvent t)
//                {
//                    // Code to run once runFactory() is completed **successfully**
//                    nextFunction()   // also invokes a Task.
//                }
//            });
//
//            runFactoryTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
//                @Override
//                public void handle(WorkerStateEvent t)
//                {
//                    // Code to run once runFactory() **fails**
//                }
//            });
//            new Thread(runFactoryTask).start();
//        }
//    }
//import javafx.concurrent.Task;
//
//    Task task = new Task<Void>() {
//        @Override public Void call() {
//            static final int max = 1000000;
//            for (int i=1; i<=max; i++) {
//                if (isCancelled()) {
//                    break;
//                }
//                updateProgress(i, max);
//            }
//            return null;
//        }
//    };
//    ProgressBar bar = new ProgressBar();
//bar.progressProperty().bind(task.progressProperty());
//new Thread(task).start();