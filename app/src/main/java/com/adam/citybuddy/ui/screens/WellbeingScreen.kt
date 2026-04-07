package com.adam.citybuddy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.citybuddy.ui.theme.ButtonPurple
import com.adam.citybuddy.ui.theme.DeepPurple
import com.adam.citybuddy.ui.theme.SoftBg

// ── Data ──────────────────────────────────────────────────────────────────────
data class WellbeingTip(val icon: String, val title: String, val body: String)
data class WellbeingVideo(val title: String, val channel: String, val url: String, val duration: String)
data class WellbeingArticle(val title: String, val source: String, val url: String, val readTime: String)
data class WellbeingTopic(
    val label: String,
    val emoji: String,
    val color: Color,
    val tips: List<WellbeingTip>,
    val videos: List<WellbeingVideo>,
    val articles: List<WellbeingArticle>
)

val wellbeingTopics = listOf(
    WellbeingTopic(
        label = "Stress & Anxiety",
        emoji = "😮‍💨",
        color = Color(0xFF7B1FA2),
        tips = listOf(
            WellbeingTip("🌬️", "Box Breathing", "Inhale 4 counts, hold 4, exhale 4, hold 4. Repeat 4 times. Activates your parasympathetic nervous system instantly."),
            WellbeingTip("✍️", "Brain Dump", "Write every stressful thought on paper without filtering. Getting it out of your head reduces mental load significantly."),
            WellbeingTip("🚶", "5-Minute Walk", "A short outdoor walk lowers cortisol levels. Even walking around the school block helps reset your stress response."),
            WellbeingTip("📵", "Phone-Free Hour", "Designate one hour daily without your phone. Social media is a proven amplifier of anxiety in students."),
        ),
        videos = listOf(
            WellbeingVideo("How to Stop Feeling Anxious About Anxiety", "Ted-Ed", "https://www.youtube.com/watch?v=SKEqCMoTFyc", "5:22"),
            WellbeingVideo("The Science of Stress", "Kurzgesagt", "https://www.youtube.com/watch?v=v-t1Z5-oPtU", "8:17"),
            WellbeingVideo("Anxiety Relief: Beginner Breathing Exercises", "Headspace", "https://www.youtube.com/watch?v=wfDTp2GogaQ", "3:45"),
        ),
        articles = listOf(
            WellbeingArticle("How to Manage Stress in School", "Healthline", "https://www.healthline.com/health/stress/stress-at-school", "6 min read"),
            WellbeingArticle("Anxiety in Teens: What Parents and Students Should Know", "Child Mind Institute", "https://childmind.org/guide/anxiety-and-anxiety-disorders/", "8 min read"),
            WellbeingArticle("Simple Ways to Relieve Stress and Anxiety", "Healthline", "https://www.healthline.com/nutrition/16-ways-relieve-stress-anxiety", "10 min read"),
        )
    ),
    WellbeingTopic(
        label = "Sleep & Rest",
        emoji = "😴",
        color = Color(0xFF1565C0),
        tips = listOf(
            WellbeingTip("🌙", "Consistent Bedtime", "Go to bed and wake at the same time daily — even weekends. Your circadian rhythm depends on consistency, not total hours alone."),
            WellbeingTip("📵", "No Screens Before Bed", "Blue light from phones suppresses melatonin for up to 2 hours. Switch to a book or light stretching 30 minutes before sleep."),
            WellbeingTip("🌡️", "Cool Your Room", "The ideal sleep temperature is 18–20°C. A cooler environment signals your body to produce sleep hormones faster."),
            WellbeingTip("☕", "Cut Caffeine at 2pm", "Caffeine has a 6-hour half-life. A coffee at 3pm still has half its effect at 9pm, disrupting deep sleep stages."),
        ),
        videos = listOf(
            WellbeingVideo("The Science of Sleep", "TED-Ed", "https://www.youtube.com/watch?v=gedoSfZvBgE", "5:21"),
            WellbeingVideo("How to Fix Your Sleep Schedule", "Andrew Huberman", "https://www.youtube.com/watch?v=oHBRS2WINIUM", "9:03"),
            WellbeingVideo("Sleep is Your Superpower", "Matt Walker | TED", "https://www.youtube.com/watch?v=5MuIMqhT8pM", "19:18"),
        ),
        articles = listOf(
            WellbeingArticle("How Much Sleep Do Teenagers Need?", "Sleep Foundation", "https://www.sleepfoundation.org/teens-and-sleep", "5 min read"),
            WellbeingArticle("The Effects of Sleep Deprivation on Students", "Healthline", "https://www.healthline.com/health/sleep-deprivation/effects-on-body", "7 min read"),
            WellbeingArticle("Sleep Hygiene Tips That Actually Work", "Verywell Health", "https://www.verywellhealth.com/sleep-hygiene-8635596", "6 min read"),
        )
    ),
    WellbeingTopic(
        label = "Study Habits",
        emoji = "📚",
        color = Color(0xFF2E7D32),
        tips = listOf(
            WellbeingTip("🍅", "Pomodoro Technique", "Study 25 minutes, break 5. After 4 cycles, take a 20-minute break. Keeps focus sharp without burning out."),
            WellbeingTip("🧠", "Active Recall", "Instead of re-reading notes, close them and write everything you remember. This is proven to be 2x more effective than passive review."),
            WellbeingTip("📅", "Time Blocking", "Assign specific tasks to specific time slots. Vague plans like 'study tonight' rarely happen; concrete blocks do."),
            WellbeingTip("🔇", "Single-Tasking", "Multitasking reduces productivity by up to 40%. One subject, one tab, one goal at a time."),
        ),
        videos = listOf(
            WellbeingVideo("How to Study Effectively — The Science", "Thomas Frank", "https://www.youtube.com/watch?v=IlU-zDU6aQ0", "11:27"),
            WellbeingVideo("The Most Powerful Way to Remember What You Study", "Sprouts", "https://www.youtube.com/watch?v=eVajQPuRmk8", "8:02"),
            WellbeingVideo("Study Less Study Smart", "Marty Lobdell", "https://www.youtube.com/watch?v=IlU-zDU6aQ0", "59:56"),
        ),
        articles = listOf(
            WellbeingArticle("Evidence-Based Study Techniques", "Khan Academy", "https://www.khanacademy.org/college-careers-more/learnstorm-growth-mindset-activities-us/high-school-activities/a/study-strategies-articles", "5 min read"),
            WellbeingArticle("The Pomodoro Technique Explained", "Todoist Blog", "https://todoist.com/productivity-methods/pomodoro-technique", "4 min read"),
            WellbeingArticle("How to Stop Procrastinating", "Healthline", "https://www.healthline.com/health/how-to-stop-procrastinating", "8 min read"),
        )
    ),
    WellbeingTopic(
        label = "Social Relationships",
        emoji = "🤝",
        color = Color(0xFFE65100),
        tips = listOf(
            WellbeingTip("👂", "Listen First", "Most conflict comes from people feeling unheard. Before responding, try to repeat back what the other person said."),
            WellbeingTip("🗣️", "Speak Your Needs", "People can't read minds. Expressing what you need clearly is more effective than hoping others notice."),
            WellbeingTip("🚧", "Set Boundaries", "It's okay to say no to plans or requests that drain you. Healthy relationships respect boundaries."),
            WellbeingTip("📱", "Quality Over Quantity", "A few deep friendships are more protective of mental health than many surface-level social media connections."),
        ),
        videos = listOf(
            WellbeingVideo("The Science of Making Friends", "SciShow Psych", "https://www.youtube.com/watch?v=HWfmBX_ZNMU", "6:14"),
            WellbeingVideo("How to Have Better Conversations", "Celeste Headlee | TED", "https://www.youtube.com/watch?v=R1vskiVDwl4", "11:44"),
            WellbeingVideo("Why We Should Talk to Strangers More", "Kio Stark | TED", "https://www.youtube.com/watch?v=rIABnFKOKuc", "11:31"),
        ),
        articles = listOf(
            WellbeingArticle("How to Build Stronger Friendships", "Greater Good Science Center", "https://greatergood.berkeley.edu/article/item/how_to_build_stronger_friendships", "6 min read"),
            WellbeingArticle("Setting Healthy Boundaries in Relationships", "Psych Central", "https://psychcentral.com/relationships/healthy-boundaries", "7 min read"),
            WellbeingArticle("The Importance of Social Connection for Mental Health", "Mind.org.uk", "https://www.mind.org.uk/information-support/tips-for-everyday-living/friendship-and-mental-health/", "5 min read"),
        )
    ),
    WellbeingTopic(
        label = "Self-Care",
        emoji = "🌿",
        color = Color(0xFF00897B),
        tips = listOf(
            WellbeingTip("🏃", "Move Daily", "Even 20 minutes of light exercise releases endorphins that improve mood for up to 12 hours afterward."),
            WellbeingTip("🥗", "Eat With Intention", "Skipping meals or relying on sugary snacks during exams crashes your energy and focus. Fuel your brain properly."),
            WellbeingTip("🎨", "Creative Outlet", "Drawing, music, writing — any creative activity reduces cortisol. It doesn't need to be good; it just needs to happen."),
            WellbeingTip("🌞", "Morning Sunlight", "10 minutes of morning sunlight regulates your sleep hormone and boosts serotonin production for the day."),
        ),
        videos = listOf(
            WellbeingVideo("Self-Care Isn't Selfish", "Psych2Go", "https://www.youtube.com/watch?v=9FpLQX_OABs", "7:32"),
            WellbeingVideo("The Art of Doing Nothing", "Veritasium", "https://www.youtube.com/watch?v=LKPwKFigF8U", "12:13"),
            WellbeingVideo("How Exercise Affects Your Brain", "TED-Ed", "https://www.youtube.com/watch?v=DsVzKCk064g", "5:36"),
        ),
        articles = listOf(
            WellbeingArticle("Self-Care Strategies for Students", "Verywell Mind", "https://www.verywellmind.com/self-care-strategies-overall-stress-reduction-3144729", "6 min read"),
            WellbeingArticle("Mindfulness for Beginners", "Mindful.org", "https://www.mindful.org/mindfulness-for-beginners/", "5 min read"),
            WellbeingArticle("The Benefits of Journaling for Mental Health", "Healthline", "https://www.healthline.com/health/benefits-of-journaling", "5 min read"),
        )
    ),
)

// ── Wellbeing Screen ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellbeingScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var contentVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(selectedTab) {
        contentVisible = false
        kotlinx.coroutines.delay(80)
        contentVisible = true
    }

    val topic = wellbeingTopics[selectedTab]

    Column(Modifier.fillMaxSize().background(SoftBg)) {
        // ── Top bar ────────────────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(DeepPurple, ButtonPurple)))
        ) {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Well-being Tips",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null,
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                // Topic tab row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    wellbeingTopics.forEachIndexed { index, t ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (selectedTab == index)
                                    Color.White.copy(0.25f) else Color.Transparent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(t.emoji, fontSize = 16.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        t.label,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedTab == index)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Content ────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 8 }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Topic hero banner
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(topic.color, topic.color.copy(alpha = 0.6f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(topic.emoji, fontSize = 48.sp)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                topic.label,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "${topic.tips.size} tips  •  ${topic.videos.size} videos  •  ${topic.articles.size} articles",
                                color = Color.White.copy(0.8f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Tips ───────────────────────────────────────────────────
                SectionHeader("💡 Tips")
                Spacer(Modifier.height(10.dp))
                topic.tips.forEachIndexed { i, tip ->
                    TipCard(tip = tip, accentColor = topic.color, index = i)
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(8.dp))

                // ── Videos ────────────────────────────────────────────────
                SectionHeader("🎬 Watch")
                Spacer(Modifier.height(10.dp))
                topic.videos.forEach { video ->
                    VideoCard(video = video, accentColor = topic.color) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                        context.startActivity(intent)
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(8.dp))

                // ── Articles ──────────────────────────────────────────────
                SectionHeader("📖 Read")
                Spacer(Modifier.height(10.dp))
                topic.articles.forEach { article ->
                    ArticleCard(article = article) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                        context.startActivity(intent)
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = DeepPurple
    )
}

// ── Tip Card ──────────────────────────────────────────────────────────────────
@Composable
private fun TipCard(tip: WellbeingTip, accentColor: Color, index: Int) {
    var expanded by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (expanded) 1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tipScale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(accentColor.copy(0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tip.icon, fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(tip.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepPurple)
                    if (!expanded) {
                        Text(
                            tip.body,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(0.12f)
                ) {
                    Text(
                        if (expanded) "▲" else "▼",
                        fontSize = 10.sp,
                        color = accentColor,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(12.dp))
                    Text(tip.body, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 22.sp)
                }
            }
        }
    }
}

// ── Video Card ────────────────────────────────────────────────────────────────
@Composable
private fun VideoCard(video: WellbeingVideo, accentColor: Color, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(56.dp)
                    .background(
                        Brush.radialGradient(listOf(accentColor.copy(0.3f), accentColor.copy(0.05f))),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayCircle,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    video.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepPurple,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(video.channel, fontSize = 12.sp, color = Color.Gray)
                    Text("  •  ", fontSize = 12.sp, color = Color.Gray)
                    Text(video.duration, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                null,
                tint = Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Article Card ──────────────────────────────────────────────────────────────
@Composable
private fun ArticleCard(article: WellbeingArticle, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    article.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepPurple,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF3E5F5)
                    ) {
                        Text(
                            article.source,
                            fontSize = 11.sp,
                            color = ButtonPurple,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(article.readTime, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                null,
                tint = Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}