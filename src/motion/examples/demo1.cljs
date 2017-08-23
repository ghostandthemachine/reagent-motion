(ns motion.examples.demo1
  (:require [reagent.core :as reagent]
            [motion.motion :as m]))


(defn view
  []
  (let [state* (reagent/atom {:x 250 :y 300})]
    (fn []
      (let [state @state*]
        [:div.demo1
         [m/staggered-motion
          {:default-styles (repeat 6 {:x 0 :y 0})
           :styles (fn [xs]
                     (let [res (map-indexed
                                 (fn [i _]
                                   (if (zero? i)
                                     state
                                     {:x (m/spring (-> xs (nth (dec i)) :x)
                                                   (m/presets :gentle))
                                      :y (m/spring (-> xs (nth (dec i)) :y)
                                                   (m/presets :gently))}))
                                 xs)]
                       res))}
          ;; Parent container
          [:div.staggered-motion-test
           {:style {:width "80%"
                    :height "80%"
                    :position "absolute"}
            :on-mouse-move #(swap! state* assoc :x (.-pageX %)
                                   :y (.-pageY %))
            :on-touch-move (fn [xs]
                             (let [e  (aget (.-touches xs) 0)]
                               (swap! state*
                                      assoc
                                      :x (.-pageX e)
                                      :y (.-pageY e))))}
           ;; Child Render function
           (fn [i {:keys [x y] :as style}]
             [:div
              {:style {:-webkit-transform (str "translate3d("
                                               (- x 35) "px, "
                                               (- y 35) "px, 0)")
                       :transform (str "translate3d("
                                       (- x 35) "px, "
                                       (- y 35) "px, 0)")
                       :z-index (- 5 i)
                       :background-image (str "url(https://github.com/ducky427/reagent-motion-demos/blob/master/resources/public/img/" i ".jpg?raw=true)")
                       :border-radius "99px"
                       :background-color "white"
                       :width "50px"
                       :height "50px"
                       :border "3px solid white"
                       :position "absolute"
                       :background-size "50px"}}])]]]))))
