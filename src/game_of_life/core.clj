(ns game-of-life.core
  (:require [hyperfiddle.rcf :refer [tests]]))

(def directions [[-1 -1] [-1 0] [-1 1]
                 [ 0 -1]        [ 0 1]
                 [ 1 -1] [ 1 0] [ 1 1]])

(defn count-neighbors
  "Count the number of live neighbors for a given cell"
  [board x y]
  (let [neighbors (map (fn [[dx dy]]
                         (get-in board [(+ x dx) (+ y dy)]))
                       directions)]
    (count (filter true? neighbors))))

(defn next-state
  "Compute the next state for a cell"
  [board x y]
  (let [cell (get-in board [x y])
        n (count-neighbors board x y)]
    (cond
      (and cell (< n 2)) false
      (and cell (> n 3)) false
      (and (not cell) (= n 3)) true
      :else cell)))

(defn next-board
  "Compute the next state of the board"
  [board]
  (mapv
   (fn [row x]
     (mapv
      (fn [_ y]
        (next-state board x y))
      row (range (count row))))
   board (range (count board))))

(defn print-board
  "Print the board to stdout"
  [board]
  (doseq [row board]
    (->> row
         (map #(if % "#" "."))
         (apply str)
         (println)))
  (println))

(defn init-board
  "Initialize a board of size (rows x cols) with random cells"
  [rows cols]
  (mapv (fn [_]
          (mapv (fn [_]
                  (= (rand-int 2) 1))
                (range cols)))
        (range rows)))

(defn -main
  "Run the Game of Life"
  [& args]
  (let [rows 20
        cols 40
        steps 100
        initial-board (init-board rows cols)]
    (loop [board initial-board
           step 0]
      (print-board board)
      (Thread/sleep 500)
      (when (< step steps)
        (recur (next-board board) (inc step))))))
