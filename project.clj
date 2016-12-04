(defproject arcadia.nrepl "0.1.0-SNAPSHOT"
  :description "nREPL bridge for Arcadia Unity"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [cider/cider-nrepl "0.14.0"]
                 [proto-repl "0.3.1"]]
  :main ^:skip-aot arcadia.nrepl
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
