(defproject clj-sandbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 ;[org.clojure/clojurescript "1.9.946"]

                 [com.rpl/specter "1.1.0"]

                 [quinto "0.1.0-SNAPSHOT"]
                 ;[figwheel-sidecar "0.5.0"]

                 ]
  :main ^:skip-aot clj-sandbox.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
