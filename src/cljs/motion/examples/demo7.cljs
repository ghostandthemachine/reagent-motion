(ns motion.examples.demo7
  (:require [reagent.core :as reagent]
            [motion.motion :as m]
            [motion.util :as util]))


(def INITIAL-STATE
  {:todos [{:key "t1"
            :data {:text "Board the plane"
                   :done? false}}
           {:key "t2"
            :data {:text "Sleep"
                   :done? false}}
           {:key "t3"
            :data {:text "Try to finish conference slides"
                   :done? false}}
           {:key "t4"
            :data {:text "Eat cheese and drink wine"
                   :done? false}}
           {:key "t5"
            :data {:text "Go around in Uber"
                   :done? false}}
           {:key "t6"
            :data {:text "Talk with conf attendees"
                   :done? false}}
           {:key "t7"
            :data {:text "Show Demo 1"
                   :done? false}}
           {:key "t8"
            :data {:text "Show Demo 2"
                   :done? false}}
           {:key "t9",
            :data {:text "Lament about the state of animation"
                   :done? false}}
           {:key "t10"
            :data {:text "Show Secret Demo"
                   :done? false}}
           {:key "t11"
            :data {:text "Go home"
                   :done? false}}]
   :value ""
   :selected :all})


(defn- handle-change
  [state* ev]
  (swap! state* assoc :value (-> ev .-target .-value)))


(defn- handle-submit
  [state* ev]
  (.preventDefault ev)
  (swap! state*
         (fn [{:keys [value] :as state}]
           (-> state
             (update :todos
                     #(into [] (concat [{:key  (str "t" (util/now))
                                         :data {:text  value
                                                :done? false}}]
                                       %)))
             (assoc :value "")))))


(defn- handle-done
  [state* done-key ev]
  (swap! state* update :todos
         (fn [todos]
           (mapv
             (fn [{:keys [key data] :as todo}]
               (let [{:keys [text done?]} data]
                 (if (= key done-key)
                   {:key key
                    :data (update data :done? not)}
                   todo)))
             todos))))


(defn- handle-toggle-all
  [state*]
  (swap! state*
         (fn [{:keys [todos] :as state}]
           (let [all-done? (every? #(-> % :data :done?) todos)]
             (update state :todos
                     (fn [todos]
                       (mapv
                         (fn [todo]
                           (assoc todo :done? (not all-done?)))
                         todos)))))))


(defn- handle-select
  [state* selected]
  (swap! state* assoc :selected selected))


(defn- handle-clear-completed
  [state*]
  (swap! state* update :todos #(remove :done? %)))


(defn- handle-destroy
  [state* date]
  (swap! state* update :todos (fn [todos] (into [] (remove #(= (:key %) date) todos)))))


(defn- get-default-styles
  [state*]
  (mapv
    (fn [todo]
      (assoc todo :style {:height  0
                          :opacity 1}))
    (:todos @state*)))


(defn- get-styles
  [state*]
  (let [{:keys [todos value selected]} @state*]
    (->> todos
      (filter
        (fn [{:keys [data]}]
          (let [{:keys [done? text]} data]
            (and (>= (.indexOf text (.toUpperCase value)) 0)
                 (or (= :all selected)
                     (and done? (= :completed selected))
                     (and (not done?) (= :active selected)))))))
      (mapv
        (fn [todo]
          (assoc todo :style {:height  (m/spring 60 (m/presets :gentle))
                              :opacity (m/spring  1 (m/presets :gentle))}))))))


(defn- will-enter
  []
  {:height  0
   :opacity 1})


(defn- will-leave
  []
  {:height  (m/spring 0)
   :opacity (m/spring 0)})


(defn view
  []
  (let [state* (reagent/atom INITIAL-STATE)]
    (fn []
      (let [{:keys [selected todos value]} @state*
            items-left  (->> todos
                          (filter #(-> % :data :done? not))
                          count)]
        [:section.todoapp
         [:header.header
          [:h1 "todos"]
          [:form
           {:on-submit (partial handle-submit state*)}
           [:input.new-todo
            {:auto-focus true
             :placeholder "What needs to be done?"
             :value value
             :on-change (partial handle-change state*)}]]]
         [:section.main
          [:input.toggle-all
           {:type :checkbox
            :checked (zero? items-left)
            :style {:display (if (-> todos count zero?)
                               :none
                               :inline)}
            :on-change (partial handle-toggle-all state*)}]
          [m/transition-motion
           {:default-styles (get-default-styles state*)
            :styles (get-styles state*)
            :will-leave (partial will-leave state*)
            :will-enter (partial will-enter state*)}
           [:ul.todo-list
            (fn [{:keys [data key style]}]
              (let [{:keys [done? text]} data]
                [:li
                 {:style style
                  :class (when done? "completed")}
                 [:div.view
                  [:input.toggle
                   {:type :checkbox
                    :on-change (partial handle-done state* key)
                    :checked done?}]
                  [:label text]
                  [:button.destroy
                   {:on-click (partial handle-destroy state* key)}]]]))]]]
         [:footer.footer
          [:span.todo-count
           [:strong items-left]
           (str " item" (when (not= items-left 1) "s") " left")]
          [:ul.filters
           [:li
            [:a
             {:class (when (= selected :all) "selected")
              :on-click (partial handle-select state* :all)}
             "All"]]
           [:li
            [:a
             {:class (when (= selected :active) "selected")
              :on-click (partial handle-select state* :active)}
             "Active"]]
           [:li
            [:a
             {:class (when (= selected :completed) "selected")
              :on-click (partial handle-select state* :completed)}
             "Completed"]]]

          [:button.clear-completed
           {:on-click (partial handle-clear-completed state*)}
           "Clear completed"]]]))))
