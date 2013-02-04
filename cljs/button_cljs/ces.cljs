;;----------------------------------------------------------------------
;; File ces.cljs
;; Written by Chris Frisz
;; 
;; Created  2 Feb 2013
;; Last modified  3 Feb 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns button-cljs.ces
  (:require [domina :refer (append! by-id log set-text!)]
            [button-cljs.game-state :refer (get-button-on get-hit get-miss)]
            [button-cljs.util :refer (cur-time)]))

;;--------------------------------------------------
;; Components

(defn- add-game-element
  [this-name]
  (let [id (:value this-name)]
    (when-not (by-id id)
      (append! (by-id "game") (str "<p id=\"" id "\"></p>")))))

(defn init
  ([priority]
     (init priority add-game-element))
  ([priority fn]
     {:priority priority, :fn fn, :depends [:this-name]}))

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
(def button-width 50)
(def button-height 50)
(def button-x 225)
(def button-y 225)
(def button-on-color "#FF0000")
(def button-off-color "#800000")
(add-entity {:init (init 1),
             :render (render (fn [game-state & {:keys [this-name button]}]
                               (let [ctx (.getContext (by-id "gameCanvas") "2d")]
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
(add-entity {:init (init 2),
             :render (render (fn [game-state & {:keys [this-name]}]
                               (set-text! (by-id (:value this-name))
                                 (str "Hits: " (get-hit game-state))))
                             1),
             :this-name (this-name "hit"),
             :score (score)})

;; MISS 
(add-entity {:init (init 3)
             :render (render (fn [game-state & {:keys [this-name]}]
                               (set-text! (by-id (:value this-name))
                                 (str "Misses: " (get-miss game-state))))
                             1),
             :this-name (this-name "miss"),
             :score (score)})

;; CANVAS
(def canvas-width 500)
(def canvas-height 500)
(def canvas-style "border:1px solid #FFFFFF;")
(def canvas-background "#000000")
(add-entity {:init (init 0 (fn [this-name]
                             (when-not (by-id (:value this-name))
                               (append! (by-id "game")
                                        (str "<canvas "
                                             "id=\"" (:value this-name) "\" "
                                             "width=\"" canvas-width "\" "
                                             "height=\"" canvas-height "\" "
                                             "style=\"" canvas-style "\">"
                                             "</canvas>"))))),
             :render (render (fn [_ & {:keys [this-name]}]
                               (let [canvas (by-id (:value this-name))
                                     ctx (.getContext canvas "2d")]
                                 (.clearRect ctx 0 0
                                   (.-width canvas)
                                   (.-height canvas))
                                 #_(set! (. ctx -fillStyle) "#FF0000")
                                 #_(.fillRect ctx 0 0 25 25)))
                             0)
             
             :this-name (this-name "gameCanvas")})

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
