;;----------------------------------------------------------------------
;; File ces.cljs
;; Written by Chris Frisz
;; 
;; Created  2 Feb 2013
;; Last modified 15 Mar 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns button-cljs.ces
  (:require [domina :refer (append! by-id log set-text!)]
            [button-cljs.globals :refer (canvas-id button-width button-height
                                         button-x button-y
                                         button-on-color button-off-color)]
            [button-cljs.game-state :refer (get-button-on get-hit get-miss)]
            [button-cljs.util :refer (cur-time)]))

;;--------------------------------------------------
;; Components

(defn render [fn priority & depends]
  {:fn fn, :priority priority, :depends depends})

(defn this-name [value] {:value value})

(defn score [] {:value 0})

(defn button [] {:value false})

(defn timestamp [] {:time (cur-time)})

(defn timer [remaining] {:remaining remaining})

(defn size [width height] {:width width :height height})

#_(defn update [cur-time fn & depends]
  {:last-time cur-time, :fn fn, :depends depends})

;;--------------------------------------------------
;; Entities

(def all-entities (atom []))

(defn add-entity
  [& cmp*]
  (swap! all-entities (partial apply conj) cmp*))

(defn get-entities-by
  [cmp]
  (for [entity @all-entities
        :when (get entity cmp)]
    entity))

;; BUTTON
(add-entity {:render (render (fn [game-state & {:keys [button]}]
                               (let [ctx (.getContext (by-id canvas-id) "2d")]
                                 (set! (. ctx -fillStyle)
                                       (if (get-button-on game-state)
                                           button-on-color
                                           button-off-color))
                                 (.fillRect ctx
                                   button-x
                                   button-y
                                   button-width
                                   button-height)))
                             1
                             ;; Dependencies
                             :button),
             :button (button),
             :this-name (this-name "button")})

;; HIT
(add-entity {:render (render (fn [game-state & {:keys [score]}]
                               (let [ctx (.getContext (by-id canvas-id) "2d")]
                                 (set! (. ctx -font) "20pt Callibri")
                                 (set! (. ctx -fillStyle) "black")
                                 (.fillText ctx
                                   (str "Hits: " (get-hit game-state))
                                   10
                                   35)))
                             1
                             ;; Dependencies
                             :score),
             :score (score)})

;; MISS 
(add-entity {:render (render (fn [game-state & {:keys [score]}]
                               (let [ctx (.getContext (by-id canvas-id) "2d")]
                                 (set! (. ctx -font) "20pt Callibri")
                                 (set! (. ctx -fillStyle) "black")
                                 (.fillText ctx
                                   (str "Misses: " (get-miss game-state))
                                   10
                                   70)))
                             1
                             ;; Dependencies
                             :score)             
             :score (score)})

;; CANVAS
(add-entity {:render (render (fn [_ & {:keys [this-name]}]
                               (let [canvas (by-id (:value this-name))
                                     ctx (.getContext canvas "2d")]
                                 (.clearRect ctx 0 0
                                   (.-width canvas)
                                   (.-height canvas))
                                 (set! (. ctx -fillStyle) "white")
                                 (.fillRect ctx
                                   0
                                   0
                                   (.-width canvas)
                                   (.-height canvas))))
                               0,
                               ;; Dependencies
                               :this-name)
             :this-name (this-name canvas-id)})

;;--------------------------------------------------
;; Systems

(defn init-system
  [all-e]
  (doseq [entity (sort-by (comp :priority :init) all-e)]
    ((:fn (:init entity)) (:this-name entity))))

(defn render-system
  [all-e game-state]
  (doseq [entity (sort-by (comp :priority :render) <= all-e)]
    (let [render (:render entity)]
      (apply (:fn render) game-state
             (let [depends (:depends render)]
               (interleave depends (map (partial get entity) depends)))))))

(defn update-system
  [all-e cur-time]
  (doseq [entity all-e]
    (let [update (:update entity)]
      (apply (:fn update) (- cur-time (:last-time entity))
             (let [depends (:depends update)]
               (interleave depends (map (partial get entity) depends)))))))
