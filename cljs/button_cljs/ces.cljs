;;----------------------------------------------------------------------
;; File ces.cljs
;; Written by Chris Frisz
;; 
;; Created  2 Feb 2013
;; Last modified 21 Feb 2013
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

(defn- add-game-element
  [this-name]
  (let [id (:value this-name)]
    (when-not (by-id id)
      (append! (by-id "game") (str "<p id=\"" id "\"></p>")))))

(defn render [fn priority & depends]
  {:fn fn, :priority priority, :depends (concat [:this-name] depends)})

(defn this-name [value] {:value value})

(defn score [] {:value 0})

(defn button [] {:value false})

(defn timestamp [] {:time (cur-time)})

(defn timer [remaining] {:remaining remaining})

(defn size [width height] {:width width :height height})

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
(add-entity {:render (render (fn [game-state & {:keys [this-name button]}]
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
(add-entity {:render (render (fn [game-state]
                               (let [ctx (.getContext (by-id canvas-id) "2d")]
                                 (set! (. ctx -font) "20pt Callibri")
                                 (set! (. ctx -fillStyle) "black")
                                 (.fillText ctx
                                   (str "Hits: " (get-hit game-state))
                                   10
                                   35)))
                             1),
             :score (score)})

;; MISS 
(add-entity {:render (render (fn [game-state]
                               (let [ctx (.getContext (by-id canvas-id) "2d")]
                                 (set! (. ctx -font) "20pt Callibri")
                                 (set! (. ctx -fillStyle) "black")
                                 (.fillText ctx
                                   (str "Misses: " (get-miss game-state))
                                   10
                                   70)))
                             1)             
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
                             0)})

;;--------------------------------------------------
;; Systems

(defn init-system
  [all-e]
  (doseq [entity (sort-by (comp :priority :init) all-e)]
    ((:fn (:init entity)) (:this-name entity))))

(defn render-system
  [all-e game-state]
  (doseq [entity (sort-by (comp :priority :render) <= all-e)]
    (apply (:fn (:render entity)) game-state
           (let [depends (:depends (:render entity))]
             (interleave depends (map (partial get entity) depends))))))
