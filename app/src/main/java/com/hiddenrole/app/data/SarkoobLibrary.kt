package com.hiddenrole.app.data

import com.hiddenrole.app.model.Ability
import com.hiddenrole.app.model.NightActionType
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.RoleTemplate
import com.hiddenrole.app.model.ScenarioRole
import com.hiddenrole.app.model.TeamDef

// شناسه‌ی تیم‌های سناریوی سرکوب
private const val TEAM_SORKOOB = "sarkoob_team_sorkoob"
private const val TEAM_CITIZEN = "sarkoob_team_citizen"
private const val TEAM_MOSSAD = "sarkoob_team_mossad"
private const val TEAM_MEK = "sarkoob_team_mek"

fun sarkoobTeams(): List<TeamDef> = listOf(
    TeamDef(id = TEAM_SORKOOB, name = "سرکوب", colorHex = "#B71C1C"),
    TeamDef(id = TEAM_CITIZEN, name = "شهروند", colorHex = "#1976D2"),
    TeamDef(id = TEAM_MOSSAD, name = "موساد", colorHex = "#546E7A"),
    TeamDef(id = TEAM_MEK, name = "مجاهدین خلق", colorHex = "#F9A825")
)

fun sarkoobAbilities(): List<Ability> = listOf(
    Ability(
        id = "sarkoob_ab_team_kill",
        name = "شات شبانه‌ی تیم سرکوب",
        description = "هر شب تیم سرکوب جمعاً یک نفر رو برای حذف انتخاب می‌کنن.",
        wakesAtNight = true,
        actionType = NightActionType.KILL
    ),
    Ability(
        id = "sarkoob_ab_slaughter_vali",
        name = "سلاخی",
        description = "به‌جای شات معمولی، می‌تونه یک نفر رو مستقیم و بدون امکان نجات حذف کنه. استعلامش منفیه؛ در برابر توماج مقاومه؛ در برابر ژینا آسیب‌پذیره.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        usesScaleWithPlayers = true
    ),
    Ability(
        id = "sarkoob_ab_negotiate",
        name = "مذاکره/اغفال",
        description = "بعد از حذف یکی از اعضای سرکوب، می‌تونه یک بازیکن رو اغفال یا از تصمیم روز منصرف کنه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_execution_order",
        name = "حکم اعدام",
        description = "یک‌بار در طول بازی می‌تونه حکم حذف فوری یک بازیکن رو صادر کنه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        maxUses = 1
    ),
    Ability(
        id = "sarkoob_ab_interrogation",
        name = "بازجویی",
        description = "یک‌بار در طول بازی یک بازیکن رو بازجویی می‌کنه و یه سرنخ درباره‌ش می‌گیره.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        maxUses = 1
    ),
    Ability(
        id = "sarkoob_ab_intel_question",
        name = "سوال اطلاعاتی",
        description = "هر شب می‌تونه از یک بازیکن یه سوال اطلاعاتی بپرسه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        usesScaleWithPlayers = true
    ),
    Ability(
        id = "sarkoob_ab_espionage",
        name = "جاسوسی",
        description = "از شب سوم به بعد، رسماً عضو تیم سرکوب می‌شه و می‌تونه جاسوسی کنه (این تغییر تیم رو گرداننده به‌صورت دستی مدیریت کنه).",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_night_arrest",
        name = "بازداشت شبانه",
        description = "هر شب یک بازیکن رو بازداشت می‌کنه (نمی‌تونه دو شب پشت‌سرهم یک نفر رو انتخاب کنه).",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_assassination",
        name = "ترور",
        description = "هر لحظه (چه روز چه شب) می‌تونه یک بازیکن رو ترور کنه؛ چون زمان دقیقش دست گرداننده‌ست، این قابلیت در ویزارد شب ظاهر نمی‌شه و باید توسط گرداننده به‌صورت دستی اجرا و اعمال بشه.",
        wakesAtNight = false,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_doctor_save",
        name = "نجات پزشکی",
        description = "هر شب می‌تونه یک بازیکن رو از حذف نجات بده.",
        wakesAtNight = true,
        actionType = NightActionType.SAVE,
        usesScaleWithPlayers = true
    ),
    Ability(
        id = "sarkoob_ab_revolutionary_execution",
        name = "اعدام انقلابی/سلاخی",
        description = "می‌تونه یک بازیکن مشکوک به سرکوب رو حذف کنه؛ تعداد دفعاتش معمولاً یکی کمتر از تعداد اعضای سرکوب در شروع بازیه (این محدودیت رو گرداننده به‌صورت دستی رعایت کنه).",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_hacker_inquiry",
        name = "استعلام هکری",
        description = "هر شب می‌تونه تیم واقعی یک بازیکن رو استعلام بگیره.",
        wakesAtNight = true,
        actionType = NightActionType.INVESTIGATE
    ),
    Ability(
        id = "sarkoob_ab_awakening",
        name = "بیداربخشی",
        description = "یه اثر غیرفعال که با حذف شدنِ خودِ این بازیکن فعال می‌شه (نه یه اقدام شبانه)؛ گرداننده اثرش رو دستی اجرا کنه.",
        wakesAtNight = false,
        actionType = NightActionType.NONE
    ),
    Ability(
        id = "sarkoob_ab_life_gift",
        name = "جان‌بخشی دوباره",
        description = "یک‌بار در طول بازی می‌تونه یک بازیکنِ تازه‌حذف‌شده رو به بازی برگردونه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        maxUses = 1
    ),
    Ability(
        id = "sarkoob_ab_heir",
        name = "وارث",
        description = "فقط توی دو شب اول می‌تونه نقش یک بازیکنِ حذف‌شده رو به ارث ببره (گرداننده این محدودیت رو دستی رعایت کنه).",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_armed_defense",
        name = "تفنگداری",
        description = "بسته به تعداد بازیکن‌ها، اسلحه بین چند بازیکن پخش می‌کنه که در صورت هدف قرار گرفتن، از خودشون دفاع کنن.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        usesScaleWithPlayers = true
    ),
    Ability(
        id = "sarkoob_ab_resistance_team",
        name = "تشکیل تیم مقاومت",
        description = "می‌تونه چند بازیکن رو به یه تیم مقاومتِ کوچیک دعوت کنه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_awareness",
        name = "آگاهی‌بخشی",
        description = "یک‌بار در طول بازی (معمولاً شب اول) یه سرنخ کلی درباره‌ی وضعیت بازی به یک بازیکن می‌ده.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        maxUses = 1
    ),
    Ability(
        id = "sarkoob_ab_guarantee",
        name = "ضمانت",
        description = "می‌تونه یک بازیکن رو ضمانت/تضمین کنه (شبیه نجات، ولی با کارکرد اجتماعی متفاوت).",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        usesScaleWithPlayers = true
    ),
    Ability(
        id = "sarkoob_ab_independent_check",
        name = "استعلام مستقلی",
        description = "هر شب یک بازیکن رو انتخاب و بررسی می‌کنه که آیا مستقل و فعال بوده یا نه؛ نتیجه‌ش رو گرداننده با توجه به نقش واقعی هدف تشخیص بده.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_guess_and_slaughter",
        name = "حدس و سلاخی",
        description = "توی دو شب مختلف می‌تونه نقش یک بازیکن رو حدس بزنه و در صورت درست بودن، حذفش کنه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        maxUses = 2
    ),
    Ability(
        id = "sarkoob_ab_inspiration",
        name = "الهام‌بخشی",
        description = "می‌تونه به تعدادی از بازیکن‌ها انگیزه/اطلاعات بده.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        usesScaleWithPlayers = true
    ),
    Ability(
        id = "sarkoob_ab_covert_op",
        name = "عملیات سری",
        description = "هر شب می‌تونه یک اقدام اطلاعاتی/مخفیانه انجام بده.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM
    ),
    Ability(
        id = "sarkoob_ab_targeted_assassination",
        name = "عملیات ترور موساد",
        description = "هر دو شب یک‌بار می‌تونه یک بازیکن رو حذف کنه (این فاصله‌ی زمانی رو گرداننده دستی رعایت کنه).",
        wakesAtNight = true,
        actionType = NightActionType.KILL
    ),
    Ability(
        id = "sarkoob_ab_mek_slaughter",
        name = "سلاخی هدفمند",
        description = "حداکثر دو بار در طول بازی می‌تونه یک بازیکن رو مستقیم حذف کنه.",
        wakesAtNight = true,
        actionType = NightActionType.CUSTOM,
        maxUses = 2
    )
)

fun sarkoobRoleTemplates(): List<RoleTemplate> = listOf(
    RoleTemplate(
        id = "sarkoob_role_vali_faqih",
        name = "ولی فقیه",
        description = "رهبر تیم سرکوب؛ تصمیم‌گیرنده‌ی نهایی برای حذف شبانه. استعلامش همیشه منفیه.",
        abilityIds = listOf("sarkoob_ab_team_kill", "sarkoob_ab_slaughter_vali")
    ),
    RoleTemplate(
        id = "sarkoob_role_zarif",
        name = "ظریف",
        description = "دیپلمات تیم سرکوب؛ بعد از حذف یکی از هم‌تیمی‌هاش می‌تونه مذاکره/اغفال کنه.",
        abilityIds = listOf("sarkoob_ab_negotiate")
    ),
    RoleTemplate(
        id = "sarkoob_role_salavati",
        name = "قاضی صلواتی",
        description = "می‌تونه یک‌بار در بازی حکم اعدام صادر کنه.",
        abilityIds = listOf("sarkoob_ab_execution_order")
    ),
    RoleTemplate(
        id = "sarkoob_role_journalist_interrogator",
        name = "بازجو خبرنگار",
        description = "یک‌بار در بازی یک بازیکن رو بازجویی می‌کنه.",
        abilityIds = listOf("sarkoob_ab_interrogation")
    ),
    RoleTemplate(
        id = "sarkoob_role_azari_jahromi",
        name = "آذری جهرمی",
        description = "هر شب یه سوال اطلاعاتی از یک بازیکن می‌پرسه.",
        abilityIds = listOf("sarkoob_ab_intel_question")
    ),
    RoleTemplate(
        id = "sarkoob_role_moaderi",
        name = "مدیری",
        description = "از شب سوم رسماً به تیم سرکوب می‌پیونده و جاسوسی می‌کنه.",
        abilityIds = listOf("sarkoob_ab_espionage")
    ),
    RoleTemplate(
        id = "sarkoob_role_radan",
        name = "سردار رادان",
        description = "هر شب یک بازیکن رو بازداشت می‌کنه.",
        abilityIds = listOf("sarkoob_ab_night_arrest")
    ),
    RoleTemplate(
        id = "sarkoob_role_saeed_emami",
        name = "سعید امامی",
        description = "هر لحظه (روز یا شب) می‌تونه یک بازیکن رو ترور کنه.",
        abilityIds = listOf("sarkoob_ab_assassination")
    ),
    RoleTemplate(
        id = "sarkoob_role_suppressor",
        name = "سرکوبگر",
        description = "عضو ساده‌ی تیم سرکوب؛ قابلیت خاصی نداره.",
        abilityIds = emptyList()
    ),
    RoleTemplate(
        id = "sarkoob_role_doctor",
        name = "دکتر قره‌حسنلو",
        description = "هر شب می‌تونه یک بازیکن رو نجات بده.",
        abilityIds = listOf("sarkoob_ab_doctor_save")
    ),
    RoleTemplate(
        id = "sarkoob_role_rahnavard",
        name = "مجیدرضا رهنورد",
        description = "می‌تونه بازیکنِ مشکوک به سرکوب رو اعدام انقلابی کنه.",
        abilityIds = listOf("sarkoob_ab_revolutionary_execution")
    ),
    RoleTemplate(
        id = "sarkoob_role_hacker",
        name = "هکر",
        description = "هر شب تیم واقعی یک بازیکن رو استعلام می‌گیره.",
        abilityIds = listOf("sarkoob_ab_hacker_inquiry")
    ),
    RoleTemplate(
        id = "sarkoob_role_zhina",
        name = "ژینا (مهسا امینی)",
        description = "با حذف شدنش، یه اثر ویژه روی تیم سرکوب فعال می‌شه (گرداننده دستی اعمال کنه).",
        abilityIds = listOf("sarkoob_ab_awakening")
    ),
    RoleTemplate(
        id = "sarkoob_role_sotoudeh",
        name = "نسرین ستوده",
        description = "یک‌بار می‌تونه یک بازیکنِ تازه‌حذف‌شده رو زنده کنه.",
        abilityIds = listOf("sarkoob_ab_life_gift")
    ),
    RoleTemplate(
        id = "sarkoob_role_gohar_eshghi",
        name = "گوهر عشقی",
        description = "توی دو شب اول می‌تونه نقش یک حذف‌شده رو به ارث ببره.",
        abilityIds = listOf("sarkoob_ab_heir")
    ),
    RoleTemplate(
        id = "sarkoob_role_militia_defender",
        name = "مجاهد کوروکور",
        description = "اسلحه بین چند بازیکن پخش می‌کنه.",
        abilityIds = listOf("sarkoob_ab_armed_defense")
    ),
    RoleTemplate(
        id = "sarkoob_role_tomaj",
        name = "توماج صالحی",
        description = "می‌تونه یه تیم مقاومت کوچیک تشکیل بده. در برابر سلاخیِ ولی‌فقیه مقاومه.",
        abilityIds = listOf("sarkoob_ab_resistance_team")
    ),
    RoleTemplate(
        id = "sarkoob_role_ronaghi",
        name = "حسین رونقی",
        description = "یک‌بار (معمولاً شب اول) آگاهی‌بخشی می‌کنه.",
        abilityIds = listOf("sarkoob_ab_awareness")
    ),
    RoleTemplate(
        id = "sarkoob_role_karimi",
        name = "علی کریمی",
        description = "می‌تونه یک بازیکن رو ضمانت کنه.",
        abilityIds = listOf("sarkoob_ab_guarantee")
    ),
    RoleTemplate(
        id = "sarkoob_role_yarrahi",
        name = "مهدی یراحی",
        description = "هر شب بررسی می‌کنه یک بازیکن مستقل و فعال بوده یا نه.",
        abilityIds = listOf("sarkoob_ab_independent_check")
    ),
    RoleTemplate(
        id = "sarkoob_role_afkari",
        name = "نوید افکاری",
        description = "توی دو شب مختلف نقش یک بازیکن رو حدس می‌زنه و در صورت درستی حذفش می‌کنه.",
        abilityIds = listOf("sarkoob_ab_guess_and_slaughter")
    ),
    RoleTemplate(
        id = "sarkoob_role_hajipour",
        name = "شروین حاجی‌پور",
        description = "به تعدادی از بازیکن‌ها الهام/انگیزه می‌ده.",
        abilityIds = listOf("sarkoob_ab_inspiration")
    ),
    RoleTemplate(
        id = "sarkoob_role_citizen",
        name = "شهروند ساده",
        description = "قابلیت خاصی نداره؛ با رأی روز به مشکوک‌ترین نفر رأی بده.",
        abilityIds = emptyList()
    ),
    RoleTemplate(
        id = "sarkoob_role_netanyahu",
        name = "بنیامین نتانیاهو",
        description = "هر شب عملیات سری انجام می‌ده و هر دو شب یک‌بار می‌تونه ترور کنه.",
        abilityIds = listOf("sarkoob_ab_covert_op", "sarkoob_ab_targeted_assassination")
    ),
    RoleTemplate(
        id = "sarkoob_role_maryam_rajavi",
        name = "مریم رجوی",
        description = "حداکثر دو بار در طول بازی می‌تونه یک بازیکن رو مستقیم حذف کنه.",
        abilityIds = listOf("sarkoob_ab_mek_slaughter")
    )
)

fun sarkoobPreset(): RolePreset {
    val teams = sarkoobTeams()
    val roles = sarkoobRoleTemplates()

    fun slot(roleId: String, teamId: String, count: Int, isFiller: Boolean = false) = ScenarioRole(
        id = "sarkoob_slot_$roleId",
        roleTemplateId = roleId,
        teamId = teamId,
        isFiller = isFiller,
        defaultCount = count
    )

    val slots = listOf(
        slot("sarkoob_role_vali_faqih", TEAM_SORKOOB, 1),
        slot("sarkoob_role_zarif", TEAM_SORKOOB, 1),
        slot("sarkoob_role_salavati", TEAM_SORKOOB, 1),
        slot("sarkoob_role_journalist_interrogator", TEAM_SORKOOB, 1),
        slot("sarkoob_role_azari_jahromi", TEAM_SORKOOB, 1),
        slot("sarkoob_role_moaderi", TEAM_SORKOOB, 1),
        slot("sarkoob_role_radan", TEAM_SORKOOB, 1),
        slot("sarkoob_role_saeed_emami", TEAM_SORKOOB, 1),
        slot("sarkoob_role_suppressor", TEAM_SORKOOB, 2),
        slot("sarkoob_role_doctor", TEAM_CITIZEN, 1),
        slot("sarkoob_role_rahnavard", TEAM_CITIZEN, 1),
        slot("sarkoob_role_hacker", TEAM_CITIZEN, 1),
        slot("sarkoob_role_zhina", TEAM_CITIZEN, 1),
        slot("sarkoob_role_sotoudeh", TEAM_CITIZEN, 1),
        slot("sarkoob_role_gohar_eshghi", TEAM_CITIZEN, 1),
        slot("sarkoob_role_militia_defender", TEAM_CITIZEN, 1),
        slot("sarkoob_role_tomaj", TEAM_CITIZEN, 1),
        slot("sarkoob_role_ronaghi", TEAM_CITIZEN, 1),
        slot("sarkoob_role_karimi", TEAM_CITIZEN, 1),
        slot("sarkoob_role_yarrahi", TEAM_CITIZEN, 1),
        slot("sarkoob_role_afkari", TEAM_CITIZEN, 1),
        slot("sarkoob_role_hajipour", TEAM_CITIZEN, 1),
        slot("sarkoob_role_citizen", TEAM_CITIZEN, 0, isFiller = true),
        slot("sarkoob_role_netanyahu", TEAM_MOSSAD, 1),
        slot("sarkoob_role_maryam_rajavi", TEAM_MEK, 1)
    )

    return RolePreset(
        id = "preset_sarkoob",
        name = "سرکوب",
        teams = teams,
        roleSlots = slots,
        nightOrder = listOf(
            "sarkoob_slot_sarkoob_role_vali_faqih",
            "sarkoob_slot_sarkoob_role_radan",
            "sarkoob_slot_sarkoob_role_doctor",
            "sarkoob_slot_sarkoob_role_hacker"
        )
    )
}
