(defproject button-cljs "0.1.0-SNAPSHOT"
  :description "A very simple ClojureScript game by Eric Dagley and Chris Frisz."
;;  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.0-RC2"]
                 [ring "1.1.6"]]
  :plugins [[lein-cljsbuild "0.2.8"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild { 
    :builds {
      :main {
        :source-path "src-cljs"
        :compiler
        {
          :output-to "resources/public/js/cljs.js"
          :optimizations :simple
          :pretty-print true
        }
        :jar true
      }
    }
  }
  :main button-cljs.server)

