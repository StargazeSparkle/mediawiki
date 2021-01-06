(defproject mediawiki "0.1.0-SNAPSHOT"
  :description "A Clojure library for the MediaWiki API."
  :url "https://github.com/StargazeSparkle/mediawiki"
  :license {:name "GPL-3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure   "1.10.1"]
                 [clj-http              "3.10.3"]
                 [org.clojure/data.json "1.0.0"]
                 [ring/ring-codec       "1.1.2"]]
  :repl-options {:init-ns mediawiki.client})
