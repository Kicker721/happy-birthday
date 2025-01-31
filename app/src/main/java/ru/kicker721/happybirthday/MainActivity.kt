package ru.kicker721.happybirthday

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.xml.image.ImageUtil
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity() : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var quizTimerTextView: TextView
    private lateinit var bigTimerTextView: TextView
    private lateinit var greetingsTextView: TextView
    private lateinit var quizLinearLayout: LinearLayout
    private lateinit var resultFrameLayout: FrameLayout
    private lateinit var congratulationsLinearLayout: LinearLayout
    private lateinit var optionButtons: List<Button>
    private lateinit var questionTextView: TextView
    private lateinit var retryTextView: TextView
    private lateinit var resultImageView: ImageView
    private lateinit var nextQuestion: Button
    private lateinit var viewKonfetti: KonfettiView
    private val startDateTimeString = "09.01.2021, 21:41"

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    private val startDateTime = LocalDateTime.parse(startDateTimeString, formatter)
    private var timerJob: Job? = null


    private var questionIndex = 0
    private var correctAnswerIndex = 0

    private lateinit var questions: List<Pair<String, List<String>>>

    private val correctAnswers = listOf(0, 2, 1)
    private val correctImages = listOf(
        R.drawable.correct,
        R.drawable.correct,
        R.drawable.correct,
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        quizTimerTextView = findViewById(R.id.quizTimerTextView)
        bigTimerTextView = findViewById(R.id.bigTimerTextView)
        greetingsTextView = findViewById(R.id.greetingsTextView)
        quizLinearLayout = findViewById(R.id.quizLayout)
        resultFrameLayout = findViewById(R.id.result)
        congratulationsLinearLayout = findViewById(R.id.congratulationsLinearLayout)
        questionTextView = findViewById(R.id.questionTextView)
        retryTextView = findViewById(R.id.retryTextView)
        resultImageView = findViewById(R.id.resultImageView)
        nextQuestion = findViewById(R.id.nextQuestion)
        viewKonfetti = findViewById(R.id.konfettiView)
        mediaPlayer = MediaPlayer.create(this, R.raw.song)
        mediaPlayer.setVolume(0.5f, 0.5f)
        nextQuestion.setOnClickListener { setupQuizQuestion() }

        questions = listOf(
            Pair(
                getString(R.string.question), listOf(
                    getString(R.string.answer),
                    getString(R.string.answer), getString(R.string.answer)
                )
            ),
            Pair(
                getString(R.string.question),
                listOf(getString(R.string.answer), getString(R.string.answer), getString(R.string.answer))
            ),
            Pair(
                getString(R.string.question), listOf(
                    getString(R.string.answer),
                    getString(R.string.answer), getString(R.string.answer)
                )
            )
        )

        optionButtons = listOf(
            findViewById(R.id.optionButton1),
            findViewById(R.id.optionButton2),
            findViewById(R.id.optionButton3)
        )

        startTimer()
    }

    private fun setupQuizQuestion() {
        nextQuestion.text = "Дальше"
        greetingsTextView.visibility = View.GONE
        if (questionIndex < questions.size) {
            quizTimerTextView.visibility = View.VISIBLE
            val question = questions[questionIndex]
            resultImageView.setImageResource(correctImages[questionIndex])
            correctAnswerIndex = correctAnswers[questionIndex]
            questionTextView.text = question.first
//            resultImageView.visibility = View.GONE
            resultFrameLayout.visibility = View.GONE
            resultImageView.visibility = View.GONE
            nextQuestion.visibility = View.GONE
            quizLinearLayout.visibility = View.VISIBLE

            question.second.forEachIndexed { index, answer ->
                optionButtons[index].text = answer
                optionButtons[index].isEnabled = true
                optionButtons[index].setOnClickListener { checkAnswer(index) }
            }

        } else {
            finishQuiz()
        }
    }

    private fun showFinalTimer() {
        bigTimerTextView.visibility = View.VISIBLE
        nextQuestion.visibility = View.GONE
        congratulationsLinearLayout.visibility = View.GONE
    }

    private fun finishQuiz() {
        quizTimerTextView.visibility = View.VISIBLE
        resultImageView.visibility = View.GONE
        quizLinearLayout.visibility = View.GONE
        congratulationsLinearLayout.visibility = View.VISIBLE
        nextQuestion.visibility = View.VISIBLE
        nextQuestion.setOnClickListener { showFinalTimer() }
        rain()

        mediaPlayer.start()
    }

    private fun checkAnswer(selectedIndex: Int) {
        if (selectedIndex == correctAnswerIndex) {
            retryTextView.visibility = View.GONE
            quizTimerTextView.visibility = View.GONE
            quizLinearLayout.visibility = View.GONE
            resultImageView.visibility = View.VISIBLE
            nextQuestion.visibility = View.VISIBLE
            questionIndex++
        } else
            retryTextView.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val now = LocalDateTime.now()
                val elapsedDuration = Duration.between(startDateTime, now)

                val days = elapsedDuration.toDays()
                val daysInYear = days % 365
                val hours = elapsedDuration.toHours() % 24
                val minutes = elapsedDuration.toMinutes() % 60
                val seconds = elapsedDuration.seconds % 60
                val years = days / 365 // TODO: handling leap years...
                // Обновляем TextView
                val formattedString = resources.getQuantityString(
                    R.plurals.years,
                    years.toInt(),
                    years
                ) + "\n" +
                        resources.getQuantityString(R.plurals.days, daysInYear.toInt(), daysInYear) + "\n" +
                        resources.getQuantityString(R.plurals.hours, hours.toInt(), hours) + "\n" +
                        resources.getQuantityString(
                            R.plurals.minutes,
                            minutes.toInt(),
                            minutes
                        ) + "\nи\n" +
                        resources.getQuantityString(R.plurals.seconds, seconds.toInt(), seconds)
                bigTimerTextView.text = formattedString
                quizTimerTextView.text = elapsedDuration.seconds.toString()
                delay(500)
            }
        }
    }
    private fun rain() {
        val drawable = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_heart)
        val drawableShape = ImageUtil.loadDrawable(drawable!!)
        viewKonfetti.start(KonfettiPresets.rain(drawableShape))
    }

}