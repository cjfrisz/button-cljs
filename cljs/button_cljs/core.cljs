;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Jan 2013
;; Last modified 27 Jan 2013
;; 
;; Code for simple button game in ClojureScript.
;;----------------------------------------------------------------------

(ns button-cljs.core
  (:require [domina :refer [append! by-class by-id log set-text!]]
            [domina.events :refer [listen!]]
            [button-cljs.comps :refer [init page-element]]
            [button-cljs.game-state :refer [make-game-state inc-score
                                            get-button-on get-hit get-miss
                                            get-last-update get-flip-time
                                            flip-button set-flip-time
                                            set-last-update new-flip-time]]
            [button-cljs.util :refer (cur-time)])
  (:require-macros [button-cljs.macros :refer [all-entities-with defentity
                                               get-entity-by-id defcomponent
                                               entity-add-comp defsystem]]))

(def game-fps 60)

(def button-id "button")
(def hit-id "hit")
(def miss-id "miss")

(def game-state (atom nil))

(declare render-game)

(letfn [(add-to-game-node [id]
          (when-not (by-id id)
            (append! game-node (str "<p id=\"" id "\"></p>"))))]
  (defentity (init 0 #(add-to-game-node button-id)))
  (defentity (init 1 #(add-to-game-node hit-id)))
  (defentity (init 2 #(add-to-game-node miss-id))))

(defn init-system
  [all-init]
  (doseq [init-fn (map :init-fn (sort-by (comp :priority :init) all-init))]
    (init-fn)))

(defn init-game 
  "Initializes the game state."
  []
  (swap! game-state make-game-state)
  #_(let [game-node (by-id "game")]
    (doseq [id [button-id hit-id miss-id]]
      (when-not (by-id id)
        (append! game-node (str "<p id=\"" id "\"></p>")))))
  (render-game))

(def controls
  "Map from key input to the effect they have on the game."
  {32 #(swap! game-state inc-score),
   82 init-game})

(defn key-handler
  "Handler for keypresses in the game."
  [key-event]
  (let [action (get controls (:keyCode key-event))]
    (when action (action))))

(listen! :keydown key-handler)
        
(defn render-game
  "Renders the game. Duh."
  []
  (set-text! (by-id button-id)
             (str "Button: " (if (get-button-on @game-state) "on" "off")))
  (set-text! (by-id hit-id) (str "Hits: " (get-hit @game-state)))
  (set-text! (by-id miss-id) (str "Misses: " (get-miss @game-state))))

(defn update-game!
  "Updates the state of the world."
  [now]
  (let [delta (- now (get-last-update @game-state))
        time-until-flip (- (get-flip-time @game-state) delta)]
    (if (<= time-until-flip 0)
        (do
          (swap! game-state flip-button)
          (swap! game-state set-flip-time (new-flip-time)))
        (swap! game-state set-flip-time time-until-flip))
    (swap! game-state set-last-update now)))

(defn game-loop
  "Main loop of the game."
  []
  (render-game)
  (update-game! (cur-time)))

;; Start the game when the window loads
(set! (.-onload js/window) init-game)
(js/setInterval game-loop (/ 1000 game-fps))
