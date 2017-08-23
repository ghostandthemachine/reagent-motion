(ns motion.examples.demo6
  (:require [reagent.core :as reagent]
            [motion.motion :as m]
            [motion.util :as util]))


(def GRID-WIDTH  150)
(def GRID-HEIGHT 150)
(def GRID (mapv
            (fn [i]
              (into [] (range 6)))
            (range 4)))


(defn- handle-mouse-down
  [state* position press ev]
  (let [page-x (.-pageX ev)
        page-y (.-pageY ev)]
    (swap! state* assoc
           :delta {:x (- page-x (:x press))
                   :y (- page-y (:y press))}
           :mouse {:x (:x press)
                   :y (:y press)}
           :pressed? true
           :last-pressed position)))


(defn- handle-touch-start
  [state* position press ev]
  (handle-mouse-down state* position press (aget (.-touches ev) 0)))


(defn- handle-mouse-move
  [state* ev]
  (let [{:keys [pressed? delta]} @state*
        page-x (.-pageX ev)
        page-y (.-pageY ev)]
    (when pressed?
      (swap! state* assoc :mouse {:x (- page-x (:x delta))
                                  :y (- page-y (:y delta))}))))


(defn- handle-touch-move
  [state* ev]
  (let [{:keys [pressed?]} @state*]
    (when pressed?
      (.preventDefault ev))
    (handle-mouse-move state* (aget (.-touches ev) 0))))


(defn- handle-mouse-up
  [state* ev]
  (swap! state* assoc
         :pressed? false
         :delta {:x 0
                 :y 0}
         :slider {:dragged nil
                  :num     0}))


(defn- handle-change
  [state* constant num ev]
  (let [{:keys [first-config]} @state*
        {:keys [stiffness damping]} first-config
        v (-> ev .-target .-value)]
    (if (= constant :stiffness)
      ;; Update stiffness
      (swap! state* assoc :first-config {:stiffness (- v (* num 30))
                                         :damping   damping})
      ;; Update damping
      (swap! state* assoc :first-config {:stiffness stiffness
                                         :damping   (- v (* num 2))}))))


(defn handle-mouse-down-input
  [state* constant num]
  (swap! state* assoc :slider {:dragged constant
                               :num     num}))


(defn view
  []
  (let [state* (reagent/atom {:delta {:x 0
                                      :y 0}
                              :mouse {:x 0
                                      :y 0}
                              :pressed? false
                              :first-config {:stiffness 60
                                             :damping    5}
                              :slider {:dragged nil
                                       :num     0}
                              :last-pressed {:x 0
                                             :y 0}})]
    (reagent/create-class
      {:component-did-mount
       (fn [this]
         (doto js/window
           (.addEventListener "mousemove" (partial handle-mouse-move state*))
           (.addEventListener "touchmove" (partial handle-touch-move state*))
           (.addEventListener "mouseup"   (partial handle-mouse-up   state*))
           (.addEventListener "touchend"  (partial handle-mouse-up   state*))))
       :reagent-render
       (fn []
         (let [{:keys [first-config
                       last-pressed
                       mouse
                       pressed?
                       slider]} @state*
               {:keys [dragged num]} slider
               {:keys [stiffness damping]} first-config]
           (into
             [:div.demo6]
             (map-indexed
               (fn [i row]
                 (map-indexed
                   (fn [j cell]
                     (let [cell-style {:top    (* GRID-HEIGHT i)
                                       :left   (* GRID-WIDTH  j)
                                       :width  GRID-WIDTH
                                       :height GRID-HEIGHT}
                           cell-stiffness (+ stiffness (* i 30))
                           cell-damping   (+ damping   (* j 2))
                           motion-style (if pressed?
                                          {:x (:x mouse)
                                           :y (:y mouse)}
                                          {:x (m/spring (- (/ GRID-WIDTH 2) 25)
                                                        {:stiffness cell-stiffness
                                                         :damping   cell-damping})
                                           :y (m/spring (- (/ GRID-HEIGHT 2) 25)
                                                        {:stiffness cell-stiffness
                                                         :damping   cell-damping})})]
                       ^{:key (str i "-" j)}
                       [:div.demo6-cell
                        {:style cell-style}
                        [:input
                         {:type :range
                          :min 0
                          :max 300
                          :value stiffness
                          :on-mouse-down (partial handle-mouse-down-input state* :stiffness i)
                          :on-change     (partial handle-change           state* :stiffness i)}]
                        [:input
                         {:type :range
                          :min 0
                          :max 40
                          :value damping
                          :on-mouse-down (partial handle-mouse-down-input state* :damping j)
                          :on-change     (partial handle-change           state* :damping j)}]
                        [m/motion
                         {:style motion-style}
                         (fn [{:keys [x y]}]
                           (let [label (if (= dragged :stiffness)
                                         (cond
                                           (< i num) [:div.demo6-minus (str "-" (* (- num i) 30))]
                                           (> i num) [:div.demo6-plus  (str "+" (* (- i num) 30))]
                                           :else [:div.demo6-plus 0])
                                         (cond
                                           (< j num) [:div.demo6-minus (str "-" (* (- num j) 2))]
                                           (> j num) [:div.demo6-plus  (str "+" (* (- j num) 2))]
                                           :else [:div.demo6-plus 0]))
                                 active? (and (= (:x last-pressed) i)
                                              (= (:y last-pressed) j))
                                 transform (str "translate3d(" x "px, " y "px, 0")]
                             [:div.demo6-ball
                              {:style {:transform         transform
                                       :-webkit-transform transform}
                               :class (when active? "demo6-ball-active")
                               :on-mouse-down  (partial handle-mouse-down  state* {:x i :y j} {:x x :y y})
                               :on-touch-start (partial handle-touch-start state* {:x i :y j} {:x x :y y})}
                              [:div.demo6-preset stiffness]
                              [:div.demo6-preset damping]]))]]))
                   row))
               GRID))))})))
