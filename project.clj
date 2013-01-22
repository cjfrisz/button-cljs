(defproject button-cljs "0.1.0-SNAPSHOT"
  :description "A very simple ClojureScript game by Eric Dagley and Chris Frisz."
;;  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.0-RC2"]
                 [ring "1.1.6"]
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [domina "1.0.1"]]
  :plugins [[lein-cljsbuild "0.2.8"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild { 
    :builds {
      :main {
        :source-path "cljs"
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

