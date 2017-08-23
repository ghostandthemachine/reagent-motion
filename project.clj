(defproject reagent-motion "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [reagent "0.6.0"]
                 [hiccup "1.0.5"]
                 [yogthos/config "0.8"]
                 [org.clojure/clojurescript "1.9.854"
                  :scope "provided"]
                 [camel-snake-kebab "0.4.0"]
                 [cljsjs/react-motion "0.5.0-0"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :min-lein-version "2.5.0"

  :uberjar-name "motion.jar"

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src"]
  :resource-paths ["resources" "target/cljsbuild"]


  :cljsbuild
  {:builds {:min
            {:source-paths ["src"]
             :compiler
             {:output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/uberjar"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src"]
             :compiler
             {:main "motion.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}}}


  :profiles {:uberjar {:prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
