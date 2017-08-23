(ns motion.examples.demo5
  (:require [reagent.core :as reagent]
            [motion.motion :as m]
            [motion.util :as util]))


(def leaving-spring-config
  {:stiffness 60
   :damping 15})


(defn- handle-circle-leave
  [state* {:keys [style]}]
  (merge
    style
    {:opacity (m/spring 0 leaving-spring-config)
     :scale   (m/spring 2 leaving-spring-config)}))



(defn- handle-mouse-move
  [state* ev]
  (swap! state* assoc
         :mouse {:x (- (.-pageX ev) 25)
                 :y (- (.-pageY ev) 25)}
         :now   (util/now)))


(defn- handle-touch-move
  [state* ev]
  (handle-mouse-move state* (aget (.-touches ev) 0)))



(defn view
  []
  (let [state* (reagent/atom {:now   (util/now)
                              :mouse {:x nil
                                      :y nil}})]
    (fn []
      (let [{:keys [mouse now] :as state} @state*
            {:keys [x y]} mouse
            styles (if (and x y)
                     [{:key now
                       :style {:opacity (m/spring 1)
                               :scale   (m/spring 0)
                               :x       (m/spring x)
                               :y       (m/spring y)}}]
                     [])]
        [:div.demo5
         [m/transition-motion
          {:will-leave (partial handle-circle-leave state*)
           :styles     styles}
          [:div
           {:style {:width  "100%"
                    :height "1000px"}
            :on-mouse-move (partial handle-mouse-move state*)
            :on-touch-move (partial handle-touch-move state*)}
           (fn [{:keys [key style] :as args}]
             (let [{:keys [opacity scale x y]} style
                   translation (str "translate3d(" x "px, " y "px, 0px) scale(" scale ")")]
               ^{:key key}
               [:div.demo5-ball
                {:style {:opacity opacity
                         :scale scale
                         :transform translation
                         :-webkit-transform translation}}]))]]]))))
