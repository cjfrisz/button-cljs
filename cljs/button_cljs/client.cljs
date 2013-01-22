;;----------------------------------------------------------------------
;; File client.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Jan 2013
;; Last modified 22 Jan 2013
;; 
;; Code for simple button game in ClojureScript.
;;----------------------------------------------------------------------

(ns button-cljs.core
  (:use [domina :only (by-class by-id log sel set-text!)]
        [domina.events :only (listen!)]))

(declare render-game)

(def game-state
  "Represents the state of the game"
  (atom {}))

(defn init-game 
  "Initializes the game state."
  []
  (swap! game-state assoc
    :button-on true,
    :hit 0,
    :miss 0,
    :last-update (.getTime (js/Date.))))

(defn show-scores
  "Publishes the score (hits and misses) via an alert."
  []
  (let [state (deref game-state)]
    (log (str "Hits: " (:hit (deref game-state)) "\n"
                   "Misses: " (:miss (deref game-state))))))

;; Event listener to display sores when the player clicks "Score"
(listen! (by-id "score") :click show-scores)

(def controls
  "Map from key input to the effect they have on the game."
  {32, #(swap! game-state
            (fn [s]
              (let [score (if (:button-on s) :hit :miss)]
                (assoc s score (inc (get s score))))))
   70, #(do
          (swap! game-state
              (fn [s]
                (assoc s :button-on (not (:button-on s)))))
          (render-game))
   82, init-game})

(defn key-handler
  "Handler for keypresses in the game."
  [key-event]
  (let [action (get controls (:keyCode key-event))]
    (when action (action))))

(listen! :keydown key-handler)

(defn render-game
  "Renders the game. Duh."
  []
  (set-text! (by-id "button")
             (str "Button " (if (:button-on (deref game-state)) "on" "off"))))

;; Start the game when the window loads
(set! (.-onload js/window) #(do (init-game) (render-game)))
