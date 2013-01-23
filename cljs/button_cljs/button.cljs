;;----------------------------------------------------------------------
;; File client.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Jan 2013
;; Last modified 23 Jan 2013
;; 
;; Code for simple button game in ClojureScript.
;;----------------------------------------------------------------------

(ns button-cljs.core
  (:use [domina :only (append! by-class by-id log set-text!)]
        [domina.events :only (listen!)]))

(def game-fps 60)

(def button-flip-min-time 1000)
(def button-flip-max-time 5000)

(def button-id "button")
(def hit-id "hit")
(def miss-id "miss")

(def game-state
  "Represents the state of the game"
  (atom {}))

(declare render-game flip-button! inc-score! make-flip-time)

(defn cur-time [] (.getTime (js/Date.)))

(defn init-game 
  "Initializes the game state."
  []
  (swap! game-state assoc
    :button-on false,
    :hit 0,
    :miss 0,
    :last-update (cur-time),
    :flip-time (make-flip-time))
  (let [game-node (by-id "game")]
    (doseq [id [button-id hit-id miss-id]]
      (when-not (by-id id)
        (append! game-node (str "<p id=\"" id "\"></p>")))))
  (render-game))

(def controls
  "Map from key input to the effect they have on the game."
  {32 #(inc-score! (if (:button-on @game-state) :hit :miss)),
   70 flip-button!,
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
             (str "Button: " (if (:button-on (deref game-state)) "on" "off")))
  (set-text! (by-id hit-id) (str "Hits: " (:hit (deref game-state))))
  (set-text! (by-id miss-id) (str "Misses: " (:miss (deref game-state)))))

(defn flip-button!
  []
  (let [cur-button (:button-on @game-state)]
    (swap! game-state assoc :button-on (not cur-button))))

(defn inc-score!
  [which-score]
  (let [cur-score (get @game-state which-score)]
    (swap! game-state assoc which-score (inc cur-score))))

(defn make-flip-time
  "Returns a number of milliseconds for the next button flip in the range
  specified by button-flip-min-time and button-flip-max-time."
  []
  (let [flip-time (rand-int button-flip-max-time)]
    (if (>= flip-time button-flip-min-time)
        flip-time
        (recur))))

(defn set-flip-time!
  ([] (swap! game-state assoc :flip-time (make-flip-time)))
  ([time] (swap! game-state assoc :flip-time time)))

(defn update-game!
  "Updates the state of the world."
  [delta]
  (let [time-until-flip (- (:flip-time @game-state) delta)]
    (if (<= time-until-flip 0)
        (do
          (flip-button!)
          (set-flip-time!))
        (set-flip-time! time-until-flip))))

(defn game-loop
  "Main loop of the game."
  []
  (let [now (cur-time)]
    (render-game)
    (update-game! (- now (:last-update (deref game-state))))
    (swap! game-state assoc :last-update now)))

;; Start the game when the window loads
(set! (.-onload js/window) init-game)
(js/setInterval game-loop (/ 1000 game-fps))
