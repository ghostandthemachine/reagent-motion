(ns motion.examples.demo4
  (:require [reagent.core :as reagent]
            [motion.motion :as m]
            [motion.util :as util]))


;; (def spring-config {:stiffness 170
;;                     :damping    26})


;; (defn make-config
;;   [config offset photos height widths]
;;   (let [n (count photos)]
;;     (loop [config    config
;;            i         0
;;            prev-left offset]
;;       (if (= i n)
;;         config
;;         (let [[w h] (nth photos i)]
;;           (recur
;;            (assoc config
;;                   i
;;                   {:left   (m/spring prev-left
;;                                      spring-config)
;;                    :height (m/spring height
;;                                      spring-config)
;;                    :width  (m/spring (* (/ height h) w)
;;                                      spring-config)})
;;            (inc i)
;;            (+ prev-left (nth widths i))))))))


;; (defn on-change
;;   [state e]
;;   (swap! state assoc :current (-> e .-target .-value (js/parseInt 10))))


;; (defn view
;;   []
;;   (let [state* (reagent/atom {:photos [[500 350]
;;                                        [800 600]
;;                                        [800 400]
;;                                        [700 500]
;;                                        [200 650]
;;                                        [600 600]]
;;                               :current 0})]
;;     (fn []
;;       (let [{:keys [photos current]} @state*]
;;         (println current photos)
;;         [:div.demo4
;;          [:input {:type "range"
;;                   :min 0
;;                   :max (dec (count photos))
;;                   :value current
;;                   :on-change (partial on-change state*)
;;                   :style {:width "200px"}}]
;;          current
;;          [m/transition-motion
;;           {:styles (let [[width height] (nth photos current)
;;                          widths         (mapv
;;                                           (fn [[w h]] (* (/ height h) w))
;;                                           photos)
;;                          offset         (reduce - 0 (subvec widths 0 current))
;;                          config         {:container {:width  (m/spring width)
;;                                                      :height (m/spring height)}}]
;;                      (make-config config offset photos height widths))}
;;           [:div.display-flex {:style {:align-items "center"
;;                                       :height "700px"
;;                                       :position "relative"}}
;;            (fn [{:keys [container] :as styles}]
;;              [:div {:style {:overflow "hidden"
;;                             :position "relative"
;;                             :margin   "auto"
;;                             :width    (:width  container)
;;                             :height   (:height container)}}
;;               (for [i  (range (count photos))
;;                     ;; :let [s (nth styles i)]
;;                     ]
;;                 (do
;;                   (println styles)
;;                   [:p i]
;;                   )
;;                 #_[:img {:style {:position "absolute"
;;                                  ;;         :background-color "lightgray"
;;                          ;;         :left (gobj/get s "left")
;;                          ;;         :height (gobj/get s "height")
;;                          ;;         :width (gobj/get s "width")}
;;                          ;; :key i
;;                          ;; :src (str "img/slider/" i ".jpg")}])])]]]))))
