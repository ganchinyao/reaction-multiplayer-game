package com.react.reactmultiplayergame.gamemode;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;
import com.react.reactmultiplayergame.helper.Constants;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * Created by gan on 5/4/17.
 * Tap when the language shown matches the correct language given
 *
 * Insane mode = 1 sec timer
 */

public class IdentifyingLanguage extends GameMode {
    private final int NUMBER_OF_ELEMENTS_IN_LANGUAGE = 31;
    private ArrayList<String[]> languageArray = new ArrayList<String[]>();
    // 0 = correct.
    private int correctOrWrong;
    private int levelDifficulty;

    @Override
    public void setCurrentQuestion(TextView player1Question, TextView player2Question) {
        player1Question.setText(R.string.identifyinglanguages);
        player2Question.setText(R.string.identifyinglanguages);
    }

    @Override
    public void setGameContent(LinearLayout rootView, RelativeLayout parentLayout, Context context, int levelDifficulty, int noOfPlayers) {
        this.levelDifficulty = levelDifficulty;
        int currentSelectionWrong;
        int currentSelection;

        //note here the colorArray we used array, not arrayList
        String colorArray [] = {
                "#FF5252", //red
                "#F48FB1", //pink
                "#C51162", //dark pink
                "#FF69B4", //Hot pink
                "#CE93D8", //light purple
                "#EA80FC", //light purple
                "#D1C4E9", //light violet
                "#B388FF", //light violet
                "#1A237E", //dark blue
                "#81D4FA", //light blue
                "#18FFFF", //cyan
                "#004D40", //dark green blue
                "#80CBC4", //light green blue
                "#00E676", //bright green
                "#76FF03", //bright green
                "#B2FF59", //bright yellow green
                "#CDDC39", //lime
                "#EEFF41", //bright lime
                "#FFEB3B", //bright yellow
                "#F9A825", //light orange
                "#FF6F00", //dark orange
                "#FF9800", //orange
                "#FF6E40", //light orange
                "#BCAAA4", //light brown
                "#CFD8DC", //light blue gray
                "#90A4AE", //dark blue gray
                "#000000", //white
                "#FFFFFF", //black
                "#FFE4C4", //bisque aka beige
                "#DEB887", //burlywood aka beige
                "#DAA520", //goldenrod
                "#9400D3", //dark violet
                "#B22222", //firebrick
                "#800000", //maroon
                "#40E0D0", //Turquiose
                "#F0E68C" //khaki
        };

        initializeLanguage();
        switch (levelDifficulty) {
            case Constants.MODE_EASY:
            case Constants.MODE_MEDIUM:
                // 33% chance of correct
                correctOrWrong = randomGenerator.nextInt(3);
                break;
            case Constants.MODE_HARD:
            case Constants.MODE_INSANE:
                // 25% chance of correct
                correctOrWrong = randomGenerator.nextInt(4);
                break;
        }

        float currentTextSize = context.getResources().getDimension(R.dimen._22ssp);
        float smallerTextSize = context.getResources().getDimension(R.dimen._18ssp);
        int _10dp = (int) context.getResources().getDimension(R.dimen._10sdp);


        // The current language selected. Use this same number that is initialize ONCE to set up top and bottom text.
        currentSelection = randomGenerator.nextInt(languageArray.size());
        // initialise Once so the text is same for both top and bottom
        int currentSelectedText = randomGenerator.nextInt(NUMBER_OF_ELEMENTS_IN_LANGUAGE - 1) + 1;


        // Account for if currentSelection is last element of array. If it is, then decrease curentSelectionWrong by 1. Otherwise just increment it by 1 to force a wrong number.
        if (currentSelection == languageArray.size() - 1) {
            currentSelectionWrong = currentSelection - 1;
        } else {
            currentSelectionWrong = currentSelection + 1;
        }

        // bottomText = the foregin language text called.
        TextView bottomText = new TextView(context);
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
        // call a random language, then in the language selected, get one of the word randomly in the array. -1 and +1 so that the result will be from 1 to the last element inclusive, excluding
        // the first element of index 0, since first element is the name of the language itself in English.

        bottomText.setText(languageArray.get(currentSelection)[currentSelectedText]);
        bottomText.setPadding(_10dp, 0, _10dp, 0);
        bottomText.setGravity(Gravity.CENTER);
        bottomText.setSingleLine();
        AutofitHelper.create(bottomText);


        // bottomTextLanguageName = i.e. Arabic, Japanese, the language name in English
        TextView bottomTextLanguageName = new TextView(context);
        bottomTextLanguageName.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallerTextSize);
        // Call the 0 index, i.e. First element of the Array to get the language name
        if (correctOrWrong == 0) {
            // correct text
            bottomTextLanguageName.setText("- " + languageArray.get(currentSelection)[0] + " -");
        } else {
            // set a wrong language name
            bottomTextLanguageName.setText("- " + languageArray.get(currentSelectionWrong)[0] + " -");
        }
        bottomTextLanguageName.setGravity(Gravity.CENTER_HORIZONTAL);
        bottomTextLanguageName.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);

        RelativeLayout.LayoutParams bottomTextParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bottomTextLanguageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout bottomOverall = new LinearLayout(context);
        bottomOverall.setOrientation(LinearLayout.VERTICAL);
        bottomOverall.setGravity(Gravity.CENTER);

        bottomOverall.addView(bottomText, bottomTextParam);
        bottomOverall.addView(bottomTextLanguageName, bottomTextLanguageParam);

        RelativeLayout.LayoutParams bottomOverallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
        bottomOverallParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        parentLayout.addView(bottomOverall, bottomOverallParam);


        //// Top view ////

        TextView topText = new TextView(context);
        topText.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);

        topText.setText(languageArray.get(currentSelection)[currentSelectedText]);
        topText.setPadding(_10dp, 0, _10dp, 0);
        topText.setGravity(Gravity.CENTER);
        topText.setSingleLine();
        topText.setRotation(180);
        AutofitHelper.create(topText);


        TextView topTextLanguageName = new TextView(context);
        topTextLanguageName.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallerTextSize);
        // Call the 0 index, i.e. First element of the Array to get the language name
        if (correctOrWrong == 0) {
            // correct text
            topTextLanguageName.setText("- " + languageArray.get(currentSelection)[0] + " -");
        } else {
            // set a wrong language name
            topTextLanguageName.setText("- " + languageArray.get(currentSelectionWrong)[0] + " -");
        }
        topTextLanguageName.setGravity(Gravity.CENTER_HORIZONTAL);
        topTextLanguageName.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
        topTextLanguageName.setRotation(180);

        RelativeLayout.LayoutParams topTextParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams topTextLanguageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout topOverall = new LinearLayout(context);
        topOverall.setOrientation(LinearLayout.VERTICAL);
        topOverall.setGravity(Gravity.CENTER);

        topOverall.addView(topTextLanguageName, topTextLanguageParam);
        topOverall.addView(topText, topTextParam);

        RelativeLayout.LayoutParams topOverallParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight() / 2);
        topOverallParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        parentLayout.addView(topOverall, topOverallParam);


        if(levelDifficulty == Constants.MODE_HARD || levelDifficulty == Constants.MODE_INSANE) {
            // change color if mode is hard/ insane
            int randomColor = Color.parseColor(colorArray[randomGenerator.nextInt(colorArray.length)]);
            bottomText.setTextColor(randomColor);
            bottomTextLanguageName.setTextColor(randomColor);
            topText.setTextColor(randomColor);
            topTextLanguageName.setTextColor(randomColor);
        } else {
            int textColor = Color.parseColor("#18FFFF");
            bottomText.setTextColor(textColor);
            bottomTextLanguageName.setTextColor(textColor);
            topText.setTextColor(textColor);
            topTextLanguageName.setTextColor(textColor);
        }



    }


    @Override
    public String getDialogTitle(Context context) {
        return context.getString(R.string.dialog_identifyinglanguage);
    }

    @Override
    public boolean player1ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player2ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player3ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public boolean player4ButtonClickedCustomExecute() {
        return false;
    }

    @Override
    public int getBackgroundMusic() {
        return R.raw.gamemode_identifyinglanguage;
    }

    @Override
    public boolean requireDefaultTimer() {
        return false;
    }

    @Override
    public boolean requireATimer() {
        return true;
    }

    @Override
    public boolean wantToShowTimer() {
        return true;
    }

    @Override
    public int customTimerDuration() {
        switch(levelDifficulty) {
            case Constants.MODE_EASY:
            case Constants.MODE_MEDIUM:
                return 2000;
            case Constants.MODE_HARD:
                return 1500;
            case Constants.MODE_INSANE:
                return 1000;
            default: return 2000;
        }
    }

    @Override
    public void removeDynamicallyAddedViewAfterQuestionEnds() {
        if(languageArray != null) {
            languageArray.clear();
            languageArray = null;
        }
    }

    @Override
    public boolean getCurrentQuestionAnswer() {
        if(correctOrWrong == 0) return true;
        else return false;
    }


    private void initializeLanguage() {
        // ** First element will be the language name, follow by 30 other words. Total of 31 elements.
        // ** THIS CLASS DOES NOT USE CODE TO TAKE INTO ACCOUNT VARYING ARRAY SIZE. UnitConversion.Java does that. HENCE, without updating to take into account, ALL array must be size 31

        //        String [] template_31_Elements = {"LanguageName", "", "", "", "", "", "", "", "", "",
//                    "", "", "", "", "", "", "", "", "", "",
//                     "", "", "", "", "", "", "", "", "", ""};

        // Make sure new language added are of 30 length. If want to change the length, make sure to change for ALL languages, as the random call to language.[random.nextInt(arrayLength)]
        // will cause index out of bound if length are not uniform throughout
        // ***** CHANGE NUMBER_OF_ELEMENT_IN_LANGUAGE at global variable if using new length ******

        String[] arabic = {"Arabic", "أَهْلاً", "مَعَ السّلامَة", "!صَبَاحُ الخَيْر", "کَيْفَ ٱلْحَال؟", "!أراكَ في مَا بَعد", "أتَحَدَّثُ قليلاً من الْعَرَبيّة", "لا أتَحَدَّثُ الْعَرَبيّة", "لا أفْهَم", "مِنْ فَضْلِك", "مبروك",
                "مِئَةٌ وَوَاْحِدٌ", "الأَرْبِعاء", "أُغُسْطُس", "أنا جائِع", "لا يفل الحديد إلا الحديد", " إذا تم العقل نقص الكلام", "إن مع اليوم غدا يا مسعدة", "تصبح على خير", "أعد من فضلك", "من فضلك!",
                " أسف", "هل تأتي هنا غالبا؟", "إني مشتاق إليك", "أنا أحبك", " ابعد!", "اتمنى لك يوما طيبا", "مساء الخير", "ما اسمك؟", " متشرف بمعرفتك", "!شـُكـْراً جَزيلاً"};

        String[] bangla = {"Bangla", "স্বাগতম", "আসসালামু আলাইকুম", "জনপ্রিয় লেখক", "স্মরনীয় বানী", "অনেক দিন দেখা নেই।", "তোর নাম কি?", "সুপ্রভাত", "শুভরাত্রি।", "ভালো থাকবেন।", "সৌভাগ্য",
                "শুভ দিন", "শ্তভযাত্রা", "বুঝতে পারি নি।", "আস্তে বলবেন কি?", "আবার ববেলন প্লিজ", "জি, একটু বলতে পারি।", "মাফ করবেন", "ধন্যবাদ", "টয়লেটটি কোথায়?", "তুমি আমার সঙ্গে নাচবে?",
                "আমি তোকে ভালোবাস", "বাঁচাও!", "আগুন লেগেছে!", "পুলিশে ডাকুন!", "শুভ বড়দিন", "শুভ জন্মদিন", "থামুন!", "অতিরিক্ত কিছুদিন", "যত সহজে বলা যায়", "এ ভুল করো না"};

        String[] chinese = {"Chinese", "冰墙，升起来吧！", "冻住！不许走！", "这个世界值得我们奋战！", " 这样可以挡住他们", "有本事就过来", "嘿，我又回来啦", "这游戏真好玩", "降龍十八掌", "其意博，其理奥，其趣深", "唯我独尊",
                "听你在那里胡扯", "弟子规，圣人训", "白发魔女", "问世间情为何物", "以彼之道还施彼身", "但愿人长久，千里共蝉娟", "那些年错过的大雨", "想问天你在哪里", "今夜的你应该明暸", "最爱你的人是我",
                "你还要我怎样", "给我一杯忘情水", "情人眼里出西施", "有情人终成眷属", "恭敬不如从命", "唯匡世经纬 胸怀天下", "知恩不报，非为人也", "风不来，树不动", "焉知来者之不如今也", "明月几时有"};

        String[] english = {"English", "It's high noon", "Justice rains from above", "Spirit Bomb", "Heroes never die!", "Goblin's Bride", "May the force be with you", "Never tell me the odds!", "The boy who lived", "The golden trio", "You shall not pass",
                "Got to catch them all!", "Fire is catching!", "Run in the Maze", "Developing my Legacy", "Which faction are you?", "You jump. I jump.", "It's time to duel!", "Why so serious?", "Elite Royal Sparky", "Space and Time",
                "Use the Force", "Let's break it down!", "Time traveller", "In you I trust", "You only live once", "Swish and Flick", "To be or not to be", "A little Love", "Forever alone", "Pass Into The Iris"};

        String[] french = {"French", "Personne n'échappe à mon regard", "Merci", "C'est si beau!", "Dans ma ligne de mire.", "Et voilà.", "Qui vivra verra", "À la carte", "Bon voyage", "Déjà-vu", "Joie de vivre",
                "Crème de la crème", "Je pense, donc, je suis", "Imaginer c'est choisir", "Tous pour un, un pour tous", "Viens me retrouver", "La vérité est dans la vin", "Bonjour", "Au revoir", "Je ne comprends pas", "S'il vous plaît",
                "Excusez-moi", "Tu parles anglais?", "Je suis enchanté", "Bonsoir", "À la vôtre !", "Vous parlez français ? ", "Je t'aime", "Ça fait longtemps !", "Bienvenue", "Bonne chance !"};

        String[] german = {"German", "Aller Anfang ist schwer", "Wer rastet, der rostet.", "Eile mit Weile", "Der Schein trügt", "Alles zu seiner Zeit", "Arzt, hilf dir selber!", "Guten Morgen!", "Willkommen!", "Einen Augenblick, bitte!", "Kommen Sie mit!",
                "Schön, Sie kennenzulernen!", "Ich mag Deutsch", "Oh! Das ist toll!", "Viel Glück!", "Entschuldigung!", "vier, fünf, sechs", "Mir ist langweilig", "Sei ruhig!", "Ich liebe dich", "Du siehst schön aus!",
                "Ach je!", "Guten Appetit!", "Bedenke das Ende.", "Helden sterben nicht!", "Schaden verstärkt", "Sicher ist sicher", "Wissen ist Macht", "Was hast Du?", "Lass mich in Ruhe!", "eins, zwei, drei"};

        String[] greek = {"Greek", "Καλώς Ορίσατε", "Γειά σας", "Λέγετε", "Πώς είστε", "Καιρό έχω να σας δω!", "Πώς σας λένε;", "Από πού είστε;", "Καλημέρα σας", "Γειά σου", "Καλὴ τύχη!",
                "Στην υγειά σου!", "Νάσαι καλά!", "Δεν καταλαβαίνω", "Δεν ξέρω", "Μιλάς Ελληνικά;", "Με συγχωρείς!", "Συγνώμη!", "Λυπάμαι!", "Παρακαλώ", "Ευχαριστώ πολύ",
                "Έρχεσαι συχνά εδώ;", "Μου λείπεις", "Σας αγαπώ", "Άφησέ με ήσυχο!", "Φωτιά!", "Χρόνια Πολλά!", "Καλά Χριστούγεννα!", "Σταματήστε!", "Πόσο κάνει αυτό;", "Μπορείτε να επαναλάβετε;"};

        String[] hebrew = {"Hebrew", "ברוך הבא", "מה שלומך?", " מזמן לא התראנו", "לילה טוב", "בהצלחה", "שיהיה לַך יום נעים", "בתיאבון", "אני לא מבין", "אני לא יודע", "אפשר לכתוב לי את זה?",
                "אתה מדבר עברית?", "בבקשה!", "סליחה!", "בבקשה", "תודה רבה", "איפה השרותים?", "אני מתגעגע אליך", "אני אוהב אותך", "החלמה מהירה", "עזוב אותי בשקט!",
                "שרפה!", "יום הולדת שמח", "די מספיק!", "לא, זה יותר מדי!", "אוהב אותי מעט אך ארוך", "מצא ידיד נאמן, מוצא אוצר", "לאהוב אות", "אהבה, תקווה, אמונה.", "הזמן עושה את שלו", "המזל אמיץ"};

        String[] italian = {"Italian", "Grazie tante", "Mi scusi", "Mi dispiace", "Non parlo italiano.", "Parla inglese?", "Come si chiama?", "Mi può aiutare?", "Buonanotte", "Buona fortuna!", "Buon viaggio!",
                "Molte grazie", "Vuole ballare con me?", "Mi manchi", "Ti amo", "Vieni qui spesso?", "Amor tutti eguaglia", "Aiutati che Dio ti aiuta", "Chi si loda, si lorda", "Il dolce far niente", "La storia si ripete",
                "Ogni cosa ha cagione", "Tal madre, tal figlia", "Quanto costa?", "Sì, un poco", "Può scriverlo, per favore?", "Capisco", "Buon appetito!", "In bocca al lupo!", "Buongiorno", "Benvenuto"};

        String[] japanese = {"Japanese", "おはようございます", "竜神の剣を喰らえ", "水のように流れ", "竜が我が敵を喰らう", "もう一戦願おう", "チャンスは自分で作るもの", "前向きにね", "二兎追うもの一兎も得ず", "失敗は成功のもと", "カカロットなど待つ必要はない",
                "あんなもんじゃない", "七転び八起き", "こうかは ばつぐんだ!", "めいちゅうりつ", "こうげき", "出る杭は打たれる", "石の上にも三年", "ごめん　なさい", "ください", "わかりません",
                "やめて！", "ありがとうございます", "いただきます", "おやすみなさい", "どうして？", "こんにちは", "継続は力なり", "苦あれば楽あり", "出る杭は打たれる", "能ある鷹は爪を隠す"};

        String[] korean = {"Korean", "게임을 하면 이겨야지", "APM 좀 올려볼까?", "날아간다", "하! 이건 사기야!", "다시 한 판 해보자고", "난 너의 곁에 있을게", "도깨비", "잊지 않겠다", "너에게 내가 가겠다", "너를 잃고 싶지 않아",
                "자꾸 가슴이 시려서", "감출 수 없는 기쁨", "피할수 없다면 즐겨라!", "제 눈에 안경이다", "궁하면 통한다", "옷이 날개다", "등잔 밑이 어둡다", "빈 수레가 요란하다", "고생 끝에 낙이 온다", "여보세요",
                "안녕하셨습니까?", "안녕하십니까", "행운을 빌어요", "좋은 하루 되세요", "감사합니다", "사랑해", "비상탈출", "모르겠습니다", "오랜만이다", "건배"};

        String[] malay = {"Malay", "Selamat datang", "Sihat selalu!", "Saya tidak tahu", "Boleh, sedikit", "Cepat jalan", "Senang diri", "Ke kiri pusing", "Hormat senjata", "Keluar baris", "Pandang ke hadapan",
                "Begerak ke kanan", "Hormat panglima", "Hentak kaki", "Julang, senjata", "Jadikan Tiga Barisan", "Bersurai", "Saya sayang kamu", "Berhenti!", "Tolong!", "Aku rindu pada mu",
                "Sama-sama", "Terima kasih", "Tolong ulang semula", "Perlahan jalan", "Rehatkan diri", "Pandai cari pelajaran", "Semoga Bahagia", "Hidup kita sia-sia", "Manusia ini kuat", "Bahasa melayu"};

        String[] portuguese = {"Portuguese", "Olá", "Está lá?", "Ótimo!", "Boa tarde", "Adeus", "Boa sorte!", "Tem um bom dia!", "Não compreendo", "Com licença!", "Desculpe!",
                "Obrigado", "Amo-o", "Ajuda! ", "Posso ajudar-lhe?", "A união faz a força", "Dos males, o menor", "Muito riso, pouco siso", "Querer é poder", "Tal pai, tal filho", "A vida começa aos 40!",
                "Quem avisa amigo é", "Eu sou português", "Eu não falo português", "Não faz mal", "Não há duas sem três", "O barato sai caro", "Paciência excede sapiência", "A curiosidade matou o gato", "Como se chama?", "Só um momento"};

        String[] russian = {"Russian", "Огонь по готовности!", "Вместе мы сила", "Без муки нет науки", "Будь что будет", "Была́ не была́", "Бог тро́ицу лю́бит", "Вода́ ка́мень то́чит", "Всему своё место", "Добрый вечер", "Привет!",
                "Неплохо!", "Спасибо", "Не за что", "Пожалуйста", "Извините", "Ничего страшного", "Пока!", "Увидимся", "Счастливого пути!", "Я не понимаю",
                "Занято́й, как пчела́", "Ищи́ ве́тра в по́ле", "Ка́пля в мо́ре", "Ку́рам на́ смех", "Лиха́ беда́ нача́ло", "О вку́сах не спо́рят", "Пан или пропа́л", "Хлеб всему́ голова́", "Че́м чёрт не шу́тит", "Что бы́ло, то прошло́"};

        String[] spanish = {"Spanish", "Buenas tardes.", "¿Cómo se llama usted?", "Estoy bien.", "Mucho gusto", "Adiós", "¿Habla usted español?", "Gracías", "¿Qué hora es?", "Yo no comprendo", "el tigre",
                "la biblioteca", "los pendientes", "El éxito llama al éxito", "El amor todo lo puede", "La risa es el mejor remedio", "Lo pasado, pasado está", "Más vale tarde que nunca", "Aún aprendo", "Hogar dulce hogar", "Qué Asi Sea",
                "¡Apagando las luces!", "Me extrañaste?", "¡Hola!", "¿Qué tal?", "Persevera y triunfaras", "A lo hecho, pecho", "Lo siento", "Hasta luego", "De nada.", "¡Buena suerte!"};

        String[] tamil = {"Tamil", "வாருங்கள்", "வணக்கம்!", "உங்கள் பெயர் என்ன? ", "மதிய வணக்கம் ", "காலை வணக்கம்", "போய் விட்டு வருகிறேன்", "நல்வாழ்த்துக்கள்", "மகிழ்ந்து உண்ணுங்கள்", "மெதுவாக பேசுங்கள்", "புரியவில்லை",
                "நீங்கள் தமிழ் பேசுவீர்களா?", "மன்னிக்க வேண்டும்", "மிகவு நன்றி", "நெருப்பு!", "காப்பாற்றுங்கள்!", "தமிழ்", "பெரிய", "அழகான", "ஆரஞ்சுப் பழம்", "வயதான",
                "இளஞ்சிவப்பு", "தமிழ் அரிச்சுவடி", "பொன்மொழிகள்", "மொழிகள்", "உள்நுழை", "ஒளியை", "பெரும்பாலானோர்", "பொன்", "புத்தாண்டு", "வாய்ப்புகள்"};

        String[] thai = {"Thai", "ยินดีต้อนรับ", "สวัสดี", "ยินดีที่ได้รู้จัก", "สวัสดีครับ", "ราตรีสวัสดิ์", "มีวันที่ดี!", "กินให้อร่อย", "เข้าใจแล้ว", "เขียนลงบนกระดาษได้ไหม?", "พูดอังกฤษได้ไหม",
                "ราคาเท่าไหร่", "ผมรักคุณ", "ผมคิดถึงคุณ", "คุณมาที่นี่บ่อยหรือเปล่า?", "หยุด!", "ภาษาเดียวไม่เคยพอ", "ไฟไหม้!", "ขอบคุณ", "ขอโทษ", "คุณพูดไทยได้ไหม",
                "กันไว้ดีกว่าแก้", "กระต่ายหมายจันทร์", "ความลับไม่มีในโลก", "กรรมตามสนอง", "แกงจืดจึงรู้คุณเกลือ", "เข้าหูซ้ายทะลุหูขวา", "ใครดีใครได้", "ใจดีสู้เสือ", "ต้นร้ายปลายดี", "น้ำนิ่งไหลลึก"};

        String[] vietnamese = {"Vietnamese", "tiếng Việt", "Xin chào", "tạm biệt", "cám ơn", "không có chi", "tiếng Anh", "Đừng nóng", "Đừng giỡn chơi", "Không cần đâu", "Nói dễ, làm khó",
                "Ngay cả, Dù cho", "Vì cái gì ?", "Mãi mãi về sau", "Gắng lên, đợi một tí", "Tôi lấy làm tiếc", "Trong lúc ấy", "Hãy thả tôi ra", "Từng tí từng tí một", "Đừng khách sáo", "Không nói chơi chứ ?",
                "Không đụng chạm", "Dạo này, lúc rày", "Bằng không, ngược bằng", "Thật Tội nghiệp", "Thiệt dó", "Có còn hơn không", "Từ từ, đừng vội", "Rất chu đáo", "Tại sao không?", "Biết mà, vậy đó"};

        if (Build.VERSION.SDK_INT >= 22) {
            languageArray.add(bangla); // bangla language not available prior
        }

        if (Build.VERSION.SDK_INT >= 21) {
            languageArray.add(korean); // somehow korean not showing on kitkat emulator...
        }

        languageArray.add(arabic);
        languageArray.add(chinese);
        languageArray.add(english);
        languageArray.add(french);
        languageArray.add(german);
        languageArray.add(greek);
        languageArray.add(hebrew);
        languageArray.add(italian);
        languageArray.add(japanese);
        languageArray.add(malay);
        languageArray.add(portuguese);
        languageArray.add(russian);
        languageArray.add(spanish);
        languageArray.add(tamil);
        languageArray.add(thai);
        languageArray.add(vietnamese);
        // order of adding doesnt matter

    }
}
