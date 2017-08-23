(ns motion.core
  (:require [reagent.core :as reagent]
            [motion.views :as views]
            [motion.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))


(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))


(defn ^:export init! []
  (dev-setup)
  (mount-root))
