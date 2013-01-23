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
  (:use [domina :only (append! by-class by-id log set-text!)]
        [domina.css :only (sel)]
        [domina.events :only (listen!)]))

(def game-fps 60)

(def button-id "button")
(def hit-id "hit")
(def miss-id "miss")

(def game-state
  "Represents the state of the game"
  (atom {}))

(declare render-game)

(defn cur-time [] (.getTime (js/Date.)))

(defn init-game 
  "Initializes the game state."
  []
  (swap! game-state assoc
    :button-on false,
    :hit 0,
    :miss 0,
    :last-update (cur-time))
  (let [game-node (by-id "game")]
    (doseq [id [button-id hit-id miss-id]]
      (when-not (by-id id)
        (append! game-node (str "<p id=\"" id "\"></p>")))))
  (render-game))

(def controls
  "Map from key input to the effect they have on the game."
  {32, #(swap! game-state
            (fn [s]
              (let [score (if (:button-on s) :hit :miss)]
                (assoc s score (inc (get s score))))))
   70, #(swap! game-state
               (fn [s]
                 (assoc s :button-on (not (:button-on s)))))
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
  (set-text! (by-id button-id)
             (str "Button: " (if (:button-on (deref game-state)) "on" "off")))
  (set-text! (by-id hit-id) (str "Hits: " (:hit (deref game-state))))
  (set-text! (by-id miss-id) (str "Misses: " (:miss (deref game-state)))))

(defn update-game
  "Updates the state of the world."
  [delta])
  

(defn game-loop
  "Main loop of the game."
  []
  (let [now (cur-time)
        last (:last-update (deref game-state))
        delta (- now last)]
    (render-game)
    (update-game delta)))

;; Start the game when the window loads
(set! (.-onload js/window) init-game)
(js/setInterval game-loop (/ game-fps 1000))
