(ns motion.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))


(doseq [i (range 2)]
  (let [k (keyword (str "motion.examples.demo" i "/state"))]
    (re-frame/reg-sub
      k
      (fn [db]
        (get db k)))))
