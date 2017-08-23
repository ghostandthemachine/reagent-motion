(ns examples.simple-transition
  (:require [reagent.core :as reagent]
            [motion.motion :as m]))


(defn view
  []
  (let [state* (reagent/atom {:open? false})]
    (fn []
      (let [{:keys [open?]} @state*]
        [:div.example-simple-transition
         [:button.btn.btn-primary
          {:on-mouse-down #(swap! state* update :open? not)
           :on-touch-start (fn [e]
                             (.preventDefault e)
                             (swap! state* update :open? not))}
          "Toggle"]
         [m/motion {:style {:x (m/spring (if open?
                                            400
                                            0))}}
          (fn [{:keys [x]}]
            [:div {:style {:border-radius "4px"
                           :background-color "rgb(240,240,232)"
                           :position "relative"
                           :margin "5px 3px 10px"
                           :width "450px"
                           :height "50px"}}
             [:div
              {:style
               {:position "absolute"
                :width "50px"
                :height "50px"
                :border-radius "4px"
                :background-color "rgb(130, 181, 198)"
                :-webkit-transform (str "translateX(" x "px)")
                :transform (str "translateX(" x "px)")}}]])]]))))
