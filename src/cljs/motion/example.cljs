(ns motion.example
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [goog.object :as gobj]
            [motion.motion :as m]))


(defn get-styles
  [state-data]
  (fn [xs]
    (.map xs (fn [_ i]
               (println "get-styles map")
               (if (zero? i)
                 (clj->js state-data)
                 #js {"x" (m/spring (-> xs
                                      (aget (dec i))
                                      (gobj/get "x"))
                                    (.-gentle m/presets))
                      "y" (m/spring (-> xs
                                      (aget (dec i))
                                      (gobj/get "y"))
                                    (.-gently m/presets))})))))


(defn Child
  [i x]
  [:div
   {:style {:-webkit-transform (str "translate3d("
                                    (- (gobj/get x "x") 25) "px, "
                                    (- (gobj/get x "y") 25) "px, 0)")
            :transform (str "translate3d("
                            (- (gobj/get x "x") 25) "px, "
                            (- (gobj/get x "y") 25) "px, 0)")
            :z-index (- 5 i)
            :background-image (str "url(https://github.com/ducky427/reagent-motion-demos/blob/master/resources/public/img/" i ".jpg?raw=true)")
            :border-radius "99px"
            :background-color "white"
            :width "50px"
            :height "50px"
            :border "3px solid white"
            :position "absolute"
            :background-size "50px"}}])


(defn Parent
  [d]
  (let [[balls state]  (:children d)]
    [:div
     {:style {:width "80%"
              :height "80%"
              :position "absolute"}
      :on-mouse-move #(swap! state
                             assoc
                             :x (.-pageX %)
                             :y (.-pageY %))
      :on-touch-move (fn [xs]
                       (let [e  (aget (.-touches xs) 0)]
                         (swap! state
                                assoc
                                :x (.-pageX e)
                                :y (.-pageY e))))}
     (for [[i x] (map-indexed vector balls)]
       ^{:key i} [Child i x])]))

(def Parent-comp (reagent/reactify-component Parent))

(defn Demo
  [state*]
  (fn [state*]
    [m/-staggered-motion {"defaultStyles" (repeat 6 {:x 0 :y 0})
                          "styles"        (get-styles @state*)}
     (fn [balls]
       (reagent/create-element Parent-comp #js {} [balls state*]))]))
