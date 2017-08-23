(ns examples.draggable-list
  (:require [reagent.core :as reagent]
            [motion.motion :as m]
            [motion.util :as util]))


(def NUM-TODOS 4)


(defn handle-todo-mouse-down
  [state* i y ev]
  (swap! state* assoc
         :delta {:x 0
                 :y (- (.-pageY ev) y)}
         :mouse {:x 0
                 :y y}
         :pressed? true
         :last-pressed i))


(defn handle-todo-touch-start
  [state* i y ev]
  (handle-todo-mouse-down state* i y (aget (.-touches ev) 0)))


(defn handle-mouse-move
  [state* ev]
  (swap! state*
         (fn [{:keys [delta last-pressed order pressed?] :as state}]
           (if pressed?
             (let [mouse {:x 0
                          :y (- (.-pageY ev) (:y delta))}
                   row (util/clamp (js/Math.round (/ (:y mouse) 100)) 0 (dec NUM-TODOS))
                   pressed-index (util/index-of order last-pressed)
                   new-order (util/re-insert order pressed-index row)]
               (assoc state
                      :mouse mouse
                      :order new-order))
             state))))


(defn handle-mouse-up
  [state ev]
  (swap! state assoc
         :pressed? false
         :delta {:x 0
                 :y 0}))


(defn handle-touch-move
  [state ev]
  (.preventDefault ev)
  (handle-mouse-move state (aget (.-touches ev) 0)))


(defn todo
  [state* style i]
  [m/motion
   {:style style}
   (fn [{:keys [scale shadow y] :as args}]
     (let [{:keys [last-pressed]} @state*]
       [:div
        {:style {:position "absolute"
                 :width "320px"
                 :height "90px"
                 :overflow "visible"
                 :pointer-events "auto"
                 :transform-origin "50% 50% 0px"
                 :border-radius "4px"
                 :color "rgb(153, 153, 153)"
                 :line-height "96px"
                 :padding-left "32px"
                 :font-size "24px"
                 :font-weight "400"
                 :background-color "rgb(255, 255, 255)"
                 :box-sizing "border-box"
                 :-webkit-box-sizing "border-box"
                 :box-shadow (str "rgba(0,0,0,0.2) 0px " shadow "px " (* 2 shadow) "px "
                                  "0px")
                 :transform (str "translateY(" y "px) "
                                 "scale(" scale ")")
                 :z-index   (if (= i last-pressed) 99 i)}
         :on-mouse-down  (partial handle-todo-mouse-down  state* i y)
         :on-touch-start (partial handle-todo-touch-start state* i y)}
        (inc i)]))])


(defn view
  []
  (let [spring-config {:stiffness 300
                       :damping    50}
        state*        (reagent/atom {:delta        {:x 0
                                                    :y 0}
                                     :last-pressed nil
                                     :mouse        {:x nil
                                                    :y nil}
                                     :order        (range NUM-TODOS)
                                     :pressed?     false})]
    (fn []
      (let [{:keys [last-pressed mouse order pressed?]} @state*]
        [:div.demo3 {:style {:cursor "url('images/cursor.png') 39 39, auto"
                             :user-select "none"
                             :background-color "#EEE"
                             :color "#FFF"
                             :position "absolute"
                             :width "100%"
                             :height "100%"
                             :font "28px/1em \"Helvetica\""
                             :align-items "center"
                             :justify-content "center"
                             :-webkit-align-items "center"
                             :-webkit-justify-content "center"}
                     :on-mouse-move (partial handle-mouse-move state*)
                     :on-mouse-up   (partial handle-mouse-up   state*)
                     :on-touch-move (partial handle-touch-move state*)
                     :on-touch-end  (partial handle-mouse-up   state*)}
         [:div {:style {:width "320px"
                        :height "400px"}}
          (for [i (range NUM-TODOS)
                :let  [j     (util/index-of order i)
                       style (if (and (= i last-pressed) pressed?)
                               {:scale  (m/spring 1.1 spring-config)
                                :shadow (m/spring 16  spring-config)
                                :y      (:y mouse)}
                               {:scale  (m/spring 1 spring-config)
                                :shadow (m/spring 1 spring-config)
                                :y      (m/spring (* 100 j)
                                                  spring-config)})]]
            ^{:key i}
            [todo state* style i])]]))))
