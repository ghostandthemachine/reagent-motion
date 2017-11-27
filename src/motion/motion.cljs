(ns motion.motion
  (:require [reagent.core :as reagent]
            [camel-snake-kebab.core :refer [->kebab-case]]
            cljsjs.react-motion))


(defn- motion-class
  [k]
  (reagent/adapt-react-class (goog.object/getValueByKeys js/ReactMotion k)))


(def -motion
  (motion-class "Motion"))


(def -staggered-motion
  (motion-class "StaggeredMotion"))


(def -transition-motion
  (motion-class "TransitionMotion"))


(def -spring
  (goog.object/getValueByKeys js/ReactMotion "spring"))

(defn spring
  ([v]
   (-spring v))
  ([v m]
   (-spring v (clj->js m))))


;; (def -spring
;;   (comp #(js->clj % :keywordize-keys true)
;;         (goog.object/getValueByKeys js/ReactMotion "spring")))


;; (defn spring
;;   ([v]
;;    (-spring v))
;;   ([v m]
;;    (-spring v (clj->js m))))


(def -presets
  (goog.object/getValueByKeys js/ReactMotion "presets"))


(defn presets
  [k]
  (aget -presets (name k)))


(def strip-style
  (goog.object/getValueByKeys js/ReactMotion "stripStyle"))


(def reorder-keys
  (goog.object/getValueByKeys js/ReactMotion "reorderKeys"))


;;; Helpers ;;;;


(defn- ->clj
  [x]
  (js->clj x :keywordize-keys true))


(defn- motion-comp
  [component args]
  (let [{:keys [children]} (js->clj args :keywordize-keys true)
        [args cs] children]
    (apply vector component args cs)))


(defn- staggered-motion-comp
  [component child args]
  (let [{:keys [children]} (js->clj args :keywordize-keys true)
        [styles] children]
    (into
      component
      (map-indexed
        (fn [i style]
          [child i style])
        styles))))


(defn- transition-motion-comp
  [component child args]
  (let [{:keys [children]} (js->clj args :keywordize-keys true)
        [styles] children]
    (into
      component
      (map
        (fn [& args]
          (apply vector child args))
        styles))))


;;;; Cljs API ;;;;


(defn motion
  [props child-fn]
  [-motion
   props
   (fn [args]
     (reagent/create-element
       (reagent/reactify-component (partial motion-comp child-fn))
       #js {}
       [args]))])


(defn staggered-motion
  [props component]
  (let [parent   (->> component butlast (into []))
        child-fn (last component)
        props    (-> props
                   (update :default-styles clj->js)
                   (update :styles (fn [f]
                                     (fn [& args]
                                       (apply (comp clj->js f ->clj) args)))))]
    (assert (fn? child-fn))
    [-staggered-motion
     props
     (fn [interpolating-styles]
       (reagent/create-element
         (reagent/reactify-component (partial staggered-motion-comp parent child-fn))
         #js {}
         [interpolating-styles]))]))


(defn transition-motion
  [props component]
  (let [component (if (= (count component) 2)
                    [(first component) nil (last component)]
                    component)
        parent   (->> component butlast (into []))
        child-fn (last component)
        props    (-> props
                   (update :default-styles clj->js)
                   (update :styles clj->js)
                   (update :will-enter (fn [f]
                                         (fn [& args]
                                           (apply (comp clj->js f ->clj) args))))
                   (update :will-leave (fn [f]
                                         (fn [& args]
                                           (apply (comp clj->js f ->clj) args)))))]

    (assert (fn? child-fn))
    [-transition-motion
     props
     (fn [interpolating-styles]
       (reagent/create-element
         (reagent/reactify-component (partial transition-motion-comp parent child-fn))
         #js {}
         [interpolating-styles]))]))
