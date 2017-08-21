(ns motion.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [goog.object :as gobj]
            [motion.motion :as m]
            [motion.examples.demo0 :as demo0]
            [motion.examples.demo1 :as demo1]
            [motion.examples.demo2 :as demo2]
            [motion.examples.demo3 :as demo3]
            [motion.examples.demo4 :as demo4]
            [motion.examples.demo5 :as demo5]
            [motion.examples.demo6 :as demo6]
            [motion.examples.demo7 :as demo7]))


(defn motion-div
  [props name*]
  (let [{:keys [x]} props]
    [:div.motion-div
     [:div {:style {:font-size (str (+ 10 x) "px")}}
      (str "Hello from " @name* " ")]]))


(defn main-panel
  []
  (let [name* (re-frame/subscribe [:name])
        ]
    ;; [:div
    ;;  [m/motion
    ;;   {:render motion-div
    ;;    :default-style {:x 5}
    ;;    :style {:x (m/spring 40)}}
    ;;   name*]]


    ;; [demo0/view]
    ;; [demo1/view]
    ;; [demo2/view]
    ;; [demo3/view]
    ;; [demo4/view]
    ;; [demo5/view]
    ;; [demo6/view]
    [demo7/view]
    ))
