package com.example.diceroll

import android.animation.Animator
import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.diceroll.databinding.ActivityMainBinding
import kotlin.random.Random
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var currentPlayer: Int = 1
    private var Playeronescore: Int = 0
    private var Playertwoscore: Int = 0
    private var winningScore: Int = 30
    private var gameEnded: Boolean = false // To track if the game has ended

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showPlayerDialog()

        val diceSound = MediaPlayer.create(this, R.raw.dice_roll)

        binding.RollButton.setOnClickListener {
            if (!gameEnded) {  // Prevent rolling if the game is over
                diceSound.start() // Play dice sound
                rollDice() // Perform dice roll and score updates
            }
        }
    }

    private fun rollDice() {
        val random = Random.nextInt(6) + 1

        val drawableResource = when (random) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        binding.dice.setImageResource(drawableResource)

        val rollAnimation = AnimationUtils.loadAnimation(this, R.anim.dice_roll)
        binding.dice.startAnimation(rollAnimation)

        rollAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.RollButton.isEnabled = false // Disable button during animation
            }

            override fun onAnimationEnd(animation: Animation?) {
                updateScore(random)
                binding.RollButton.isEnabled = true // Re-enable button after animation
                checkWinner()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun updateScore(random: Int) {
        if (currentPlayer == 1) {
            Playeronescore += random
            binding.p1Score.text = Playeronescore.toString()
            currentPlayer = 2
        } else {
            Playertwoscore += random
            binding.p2Score.text = Playertwoscore.toString()
            currentPlayer = 1
        }
        binding.currentPlayer.text = currentPlayer.toString()
    }

    private fun checkWinner() {
        if (Playeronescore >= winningScore || Playertwoscore >= winningScore) {
            gameEnded = true // Mark the game as ended
            binding.RollButton.isEnabled = false // Disable Roll button

            // Apply dimming effect to the background
            binding.dimBackground.visibility = View.VISIBLE

            binding.confettiAnimation.visibility = View.VISIBLE
            binding.confettiAnimation.playAnimation()

            binding.confettiAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    showWinningDialog()
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun showWinningDialog() {
        val winner = if (Playeronescore >= winningScore) "Player 1" else "Player 2"
        AlertDialog.Builder(this)
            .setTitle("$winner Won 🏆")
            .setMessage("Would you like to start a new game or exit?")
            .setPositiveButton("New Game") { _, _ -> resetGame() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .show()
    }

    private fun resetGame() {
        Playeronescore = 0
        Playertwoscore = 0
        binding.p1Score.text = "0"
        binding.p2Score.text = "0"
        binding.winningText.visibility = View.INVISIBLE
        binding.confettiAnimation.visibility = View.INVISIBLE
        binding.dimBackground.visibility = View.INVISIBLE // Reset dim background
        binding.RollButton.isEnabled = true // Enable Roll button
        gameEnded = false // Reset game state
        showPlayerDialog()
    }

    private fun showPlayerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_player_info, null)
        val player1Input = dialogView.findViewById<EditText>(R.id.player1Input)
        val player2Input = dialogView.findViewById<EditText>(R.id.player2Input)
        val maxScoreInput = dialogView.findViewById<EditText>(R.id.maxScoreInput)

        AlertDialog.Builder(this)
            .setTitle("Enter Player Info")
            .setView(dialogView)
            .setPositiveButton("Start Game") { _, _ ->
                val player1Name = player1Input.text.toString().trim()
                val player2Name = player2Input.text.toString().trim()
                val maxScore = maxScoreInput.text.toString()

                binding.firstPlayer.text = if (player1Name.isEmpty()) "Player 1" else player1Name
                binding.secondPlayer.text = if (player2Name.isEmpty()) "Player 2" else player2Name

                if (maxScore.isNotEmpty()) {
                    winningScore = maxScore.toInt()
                }
            }
            .setCancelable(false)
            .show()
    }
}
