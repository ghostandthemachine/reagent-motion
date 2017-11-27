(ns examples.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [examples.simple-transition :as simple-transition]
              [examples.chat-heads :as chat-heads]
              [examples.draggable-balls :as draggable-balls]
              [examples.draggable-list :as draggable-list]
              [examples.water-ripples :as water-ripples]
              [examples.todomvc-list-transition :as todomvc-list-transition]
              [examples.photo-gallery :as photo-gallery]
              [examples.spring-parameters-chooser :as spring-parameters-chooser]))


(defn- example-link
  [page-name url]
  [:div
   [:a {:href url} page-name]])

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to examples"]
   [example-link "Simple Transition" "/demos/simple-transition"]
   [example-link "Chat Heads" "/demos/chat-heads"]
   [example-link "Draggable Balls" "/demos/draggable-balls"]
   [example-link "TodoMVC List Transition" "/demos/todomvc-list-transition"]
   [example-link "Photo Gallery" "/demos/photo-gallery"]
   [example-link "Spring Parameters Chooser" "/demos/spring-parameters-chooser"]
   [example-link "Water Ripples" "/demos/water-ripples"]
   [example-link "Draggable List" "/demos/draggable-list"]])

;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/demos/simple-transition" []
  (reset! page #'simple-transition/view))

(secretary/defroute "/demos/chat-heads" []
  (reset! page #'chat-heads/view))

(secretary/defroute "/demos/draggable-balls" []
  (reset! page #'draggable-balls/view))

(secretary/defroute "/demos/water-ripples" []
  (reset! page #'water-ripples/view))

(secretary/defroute "/demos/todomvc-list-transition" []
  (reset! page #'todomvc-list-transition/view))

(secretary/defroute "/demos/photo-gallery" []
  (reset! page #'photo-gallery/view))

(secretary/defroute "/demos/spring-parameters-chooser" []
  (reset! page #'spring-parameters-chooser/view))

(secretary/defroute "/demos/draggable-list" []
  (reset! page #'draggable-list/view))


;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn ^:export init []
  (println "init app")
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (println path (secretary/locate-route path))
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
