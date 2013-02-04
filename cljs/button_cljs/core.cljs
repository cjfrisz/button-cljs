;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Jan 2013
;; Last modified  3 Feb 2013
;; 
;; Code for simple button game in ClojureScript.
;;----------------------------------------------------------------------

(ns button-cljs.core
  (:require [domina :refer (append! by-class by-id log set-text!)]
            [domina.events :refer (listen!)]
            [button-cljs.ces :refer (all-entities get-entities-by
                                     init-system render-system)]
            [button-cljs.game-state :refer (make-game-state inc-score
                                            get-last-update get-flip-time
                                            flip-button set-flip-time
                                            set-last-update new-flip-time
                                            set-flip-time)]
            [button-cljs.util :refer (cur-time)]))

(def game-fps 60)

(def game-state (atom nil))

(declare render-game)

(defn init-game 
  "Initializes the game state."
  []
  (swap! game-state make-game-state)
  (init-system (get-entities-by :init))
  (render-system (get-entities-by :render) @game-state))

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
  (render-system (get-entities-by :render) @game-state)
  (update-game! (cur-time)))

;; Start the game when the window loads
(set! (.-onload js/window) init-game)
(js/setInterval game-loop (/ 1000 game-fps))
