(defproject com.sagevisuals/one-see "0-SNAPSHOT1"
  :description "A featherweight Clojure library for symmetric one-to-one look ups"
  :url "https://github.com/blosavio/one-see"
  :license {:name "MIT License"
            :url "https://opensource.org/license/mit"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.12.3"]]
  :repl-options {:init-ns one-see.core}
  :plugins []
  :profiles {:dev {:dependencies [[com.clojure-goes-fast/clj-async-profiler "1.6.2"]
                                  [com.sagevisuals/chlog "5"]
                                  [com.sagevisuals/readmoi "6"]]
                   :plugins [[dev.weavejester/lein-cljfmt "0.12.0"]
                             [lein-codox "0.10.8"]]
                   :jvm-opts ["-Djdk.attach.allowAttachSelf"
                              "-XX:+UnlockDiagnosticVMOptions"
                              "-XX:+DebugNonSafepoints"
                              "-Dclj-async-profiler.output-dir=./resources/profiler_data/"]}
             :benchmark {:jvm-opts ["-XX:+TieredCompilation"
                                    "-XX:TieredStopAtLevel=4"]}
             :repl {}}
  :aliases {"readmoi" ["run" "-m" "readmoi-generator"]
            "chlog" ["run" "-m" "chlog-generator"]}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [#"^one-see\.(?!scratch)"]
          :target-path "doc"
          :output-path "doc"
          :doc-files []
          :source-uri "https://github.com/blosavio/one-see/blob/master/{filepath}#L{line}"
          :html {:transforms [[:div.sidebar.primary] [:append [:ul.index-link [:li.depth-1 [:a {:href "https://github.com/blosavio/one-see"} "Project Home"]]]]]}
          :project {:name "one-see" :version "version 0"}}
  :scm {:name "git" :url "https://github.com/blosavio/one-see"})

