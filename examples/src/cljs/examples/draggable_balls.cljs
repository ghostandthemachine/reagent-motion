(ns examples.draggable-balls
  (:require [reagent.core :as reagent]
            [motion.motion :as m]
            [motion.util :as util]))

(def NUM-BALLS 11)
(def WIDTH 70)
(def HEIGHT 90)


(def layout
  (into {} (map (fn [x]
                  [x
                   [(* WIDTH (mod x 3))
                    (* HEIGHT (Math/floor (/ x 3)))]])
                (range NUM-BALLS))))


(def colors ["#EF767A" "#456990" "#49BEAA" "#49DCB1" "#EEB868"
             "#EF767A" "#456990" "#49BEAA" "#49DCB1" "#EEB868"
             "#EF767A"])

(defn handle-ball-mouse-down
  [state* i x y ev]
  (swap! state* assoc
         :last-press i
         :pressed? true
         :delta    {:x (- (.-pageX ev) x)
                    :y (- (.-pageY ev) y)}
         :mouse    {:x x
                    :y y}))


(defn handle-ball-touch-start
  [state* i x y ev]
  (handle-ball-mouse-down state* i x y (aget (.-touches ev) 0)))


(defn ball
  [state* i]
  (let [spring1 {:stiffness 180
                 :damping    10}
        spring2 {:stiffness 120
                 :damping    17}]
    (fn [state* i]
      (let [{:keys [mouse order last-press pressed?] :as state} @state*
            {:keys [x y]}  mouse
            visual-pos     (util/index-of order i)
            hit-last-ball? (and (= i last-press) pressed?)
            [[x y] style]  (if hit-last-ball?
                             [[x y] {:tx  x
                                     :ty  y
                                     :scale       (m/spring 1.2 spring1)}]
                             (let [[x y] (layout visual-pos)]
                               [[x y] {:tx  (m/spring x spring1)
                                       :ty  (m/spring y spring1)
                                       :scale       (m/spring 1 [180 10])}]))
            style            (assoc style
                                    :shadow (m/spring (/ (/ (- x (- (* 3 WIDTH) 50)) 2) 15) spring1))]
        [m/motion
         {:style style}
         (fn [{:keys [shadow tx ty scale] :as style}]
           [:div
            {:on-mouse-down  (partial handle-ball-mouse-down  state* i x y)
             :on-touch-start (partial handle-ball-touch-start state* i x y)
             :style {:position           "absolute"
                     :border             "1px solid black"
                     :border-radius      "99px"
                     :width              "50px"
                     :height             "50px"
                     :background-color   (nth colors i)
                     :-webkit-transform  (str "translate3d(" tx "px," ty "px,0) "
                                              "scale(" scale ")")
                     :transform          (str "translate3d(" tx "px," ty "px,0) "
                                              "scale(" scale ")")
                     :z-index            (if (= i last-press) 99 visual-pos)
                     :box-shadow         (str shadow "px 5px 5px rgba(0,0,0,0.5)")}}])]))))


(defn handle-mouse-move
  [state* ev]
  (swap! state*
         (fn [{:keys [pressed? delta last-press order] :as state}]
           (if pressed?
             (let [mx  (- (.-pageX ev) (:x delta))
                   my  (- (.-pageY ev) (:y delta))
                   col (util/clamp (Math/floor (/ mx WIDTH)) 0 2)
                   row (util/clamp (Math/floor (/ my HEIGHT)) 0 (Math/floor (/ NUM-BALLS 3)))
                   i   (+ (* row 3) col)]
               (if (< i NUM-BALLS)
                 (assoc state
                        :mouse {:x mx
                                :y my}
                        :order (util/re-insert order (util/index-of order last-press) i))
                 state))
             state))))


(defn handle-mouse-up
  [state* ev]
  (swap! state* assoc
         :pressed? false
         :delta {:x 0
                 :y 0}))


(defn handle-touch-move
  [state* ev]
  (.preventDefault ev)
  (handle-mouse-move state* (aget (.-touches ev) 0)))


(defn view
  []
  (let [state* (reagent/atom {:mouse        {:x nil
                                             :y nil}
                              :mouse-circle {:x nil
                                             :y nil}
                              :last-press   nil
                              :pressed?     false
                              :order        (range NUM-BALLS)})]
    (fn []
      (let [{:keys [order]} @state*]
        [:div.demo2
         {:style {:height "80%"
                  :width "80%"
                  :position "absolute"
                  :align-items "center"
                  :justify-content "center"
                  :-webkit-align-items "center"
                  :-webkit-justify-content "center"}
          :on-mouse-move (partial handle-mouse-move state*)
          :on-mouse-up   (partial handle-mouse-up   state*)
          :on-touch-move (partial handle-touch-move state*)
          :on-touch-end  (partial handle-mouse-up   state*)}
         [:div
          {:style {:width "190px"
                   :height "320px"}}
          (for [i order]
            ^{:key i}
            [ball state* i])]]))))
