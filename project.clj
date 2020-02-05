(defproject trello2xlsx "0.1.0-SNAPSHOT"
  :description "Convert Trello exported JSON to XLSX"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [dk.ative/docjure "1.12.0"]
                 [org.clojure/data.json "0.2.7"]]
  :main ^:skip-aot trello2xlsx.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
