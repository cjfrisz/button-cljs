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
  (:require [button-cljs.game-state :as gs]
            [button-cljs.util :as util])
  (:use [domina :only (append! by-class by-id log set-text!)]
        [domina.events :only (listen!)]))

(def game-fps 60)

(def button-id "button")
(def hit-id "hit")
(def miss-id "miss")

(def game-state (atom nil))

(declare render-game)

(defn init-game 
  "Initializes the game state."
  []
  (swap! game-state gs/make-game-state)
  (let [game-node (by-id "game")]
    (doseq [id [button-id hit-id miss-id]]
      (when-not (by-id id)
        (append! game-node (str "<p id=\"" id "\"></p>")))))
  (render-game))

(def controls
  "Map from key input to the effect they have on the game."
  {32 #(swap! game-state gs/inc-score),
   70 #(swap! game-state gs/flip-button),
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
             (str "Button: " (if (gs/get-button-on @game-state) "on" "off")))
  (set-text! (by-id hit-id) (str "Hits: " (gs/get-hit @game-state)))
  (set-text! (by-id miss-id) (str "Misses: " (gs/get-miss @game-state))))

(defn update-game!
  "Updates the state of the world."
  [now]
  (let [delta (- now (gs/get-last-update @game-state))
        time-until-flip (- (gs/get-flip-time @game-state) delta)]
    (if (<= time-until-flip 0)
        (do
          (swap! game-state gs/flip-button)
          (swap! game-state gs/set-flip-time (gs/new-flip-time)))
        (swap! game-state gs/set-flip-time time-until-flip))
    (swap! game-state gs/set-last-update now)))

(defn game-loop
  "Main loop of the game."
  []
  (render-game)
  (update-game! (util/cur-time)))

;; Start the game when the window loads
(set! (.-onload js/window) init-game)
(js/setInterval game-loop (/ 1000 game-fps))
