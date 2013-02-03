;;----------------------------------------------------------------------
;; File ces.cljs
;; Written by Chris Frisz
;; 
;; Created  2 Feb 2013
;; Last modified  2 Feb 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns button-cljs.ces
  (:require [domina :refer (append! by-id log set-text!)]
            [button-cljs.game-state :refer (get-button-on get-hit get-miss)]))

(def all-entities (atom []))

(defn get-entities-by
  [cmp]
  (for [entity @all-entities
        :when (get entity cmp)]
    entity))

(defn- add-game-element
  [page-element]
  (let [id (:id page-element)]
    (when-not (by-id id)
      (append! (by-id "game") (str "<p id=\"" id "\"></p>")))))

(defn add-entity
  [& cmp*]
  (swap! all-entities (partial apply conj) cmp*))

(defn init
  [priority]
  {:priority priority, :fn add-game-element, :requires [:page-element]})

(defn render [fn] {:fn fn})

(defn page-element [id] {:id id})

;; BUTTON
(add-entity {:init (init 0),
             :render {:fn (fn [game-state]
                            (set-text! (by-id "button")
                                       (str "Button: "
                                            (if (get-button-on game-state)
                                                "on"
                                                "off"))))},
             :page-element (page-element "button")})

;; HIT
(add-entity {:init (init 1),
             :render (render (fn [game-state]
                               (set-text! (by-id "hit")
                                          (str "Hits: " (get-hit game-state))))),
             :page-element (page-element "hit")})

;; MISS 
(add-entity {:init (init 2)
             :render (render (fn [game-state]
                               (set-text! (by-id "miss")
                                          (str "Misses: "
                                               (get-miss game-state))))),
             :page-element (page-element "miss")})

(defn init-system
  [all-e]
  (doseq [entity (sort-by (comp :init :priority) all-e)]
    ((:fn (:init entity)) (:page-element entity))))

(defn render-system
  [all-e game-state]
  (doseq [render-cmp (map :render all-e)]
    ((:fn render-cmp) game-state)))
