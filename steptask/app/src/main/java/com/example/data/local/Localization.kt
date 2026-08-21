package com.example.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppStrings = staticCompositionLocalOf { getAppStrings("en") }

fun getAppStrings(languageCode: String): AppStrings {
    return if (languageCode == "tr") TurkishStrings else EnglishStrings
}

interface AppStrings {
    val appName: String
    val appSubtitle: String
    val searchPlaceholder: String
    val clearSearch: String

    // Stats
    val overallProgress: String
    val totalTasks: String
    val completed: String
    val active: String

    // Filter Tabs
    val filterAll: String
    val filterActive: String
    val filterDone: String

    // Sorting
    val sortBy: String
    val sortNewest: String
    val sortProgress: String

    // Category Management
    val categoriesSection: String
    val manageCategories: String
    val manageCategoriesDesc: String
    val addCategory: String
    val newCategoryPlaceholder: String
    val renameCategory: String
    val deleteCategory: String
    val deleteCategoryConfirmTitle: String
    val deleteCategoryConfirmMsg: String
    val categoryAlreadyExists: String
    val categoryCannotBeEmpty: String
    val allCategories: String
    val selectCategory: String
    val categoryLabel: String
    val customCategoryLabel: String
    val noCategoriesFound: String
    val categoryCount: String

    // Actions & Buttons
    val newTask: String
    val blueprints: String
    val startGuide: String
    val reviewSteps: String
    val markDone: String
    val done: String
    val reopen: String
    val cancel: String
    val saveChanges: String
    val createTask: String
    val editTask: String
    val duplicateTask: String
    val resetProgress: String
    val deleteTask: String
    val deleteConfirmTitle: String
    val deleteConfirmMsg: String
    val confirmDelete: String
    val save: String
    val edit: String

    // Empty States
    val noTasksTitle: String
    val noTasksDesc: String
    val noMatchingTasks: String
    val noCompletedTasks: String
    val allTasksCompleted: String

    // Detail Screen
    val taskOverview: String
    val stepCompletion: String
    val procedureStepsHeader: String
    val tapToToggleOrFocus: String
    val noStepsAdded: String
    val addSteps: String

    // Focus Guide
    val stepOf: String
    val stepTimer: String
    val prevStep: String
    val nextStep: String
    val completeAndNext: String
    val completeAndFinish: String
    val allDone: String
    val whatToDo: String
    val howToDoIt: String
    val proTipHeader: String
    val whatToDoHeader: String
    val howToDoItHeader: String
    val tipsAndAdviceHeader: String
    val procedureCompleted: String
    val procedureCompletedDesc: String
    val exitGuide: String

    // Add / Edit Screen
    val createTaskHeader: String
    val editTaskHeader: String
    val taskDetailsHeader: String
    val taskTitleLabel: String
    val taskTitlePlaceholder: String
    val goalContextLabel: String
    val goalContextPlaceholder: String
    val stepProcedureHeader: String
    val stepProcedureDesc: String
    val stepWhatLabel: String
    val stepWhatPlaceholder: String
    val stepHowLabel: String
    val stepHowPlaceholder: String
    val stepTipsLabel: String
    val stepTipsPlaceholder: String
    val addAnotherStep: String
    val addCheckpoint: String
    val addPrepStep: String
    val moveUp: String
    val moveDown: String
    val removeStep: String
    val pleaseEnterTitle: String
    val pleaseAddStep: String

    // Settings Screen
    val settingsTitle: String
    val appearanceSection: String
    val themeModeLabel: String
    val themeSystem: String
    val themeSystemDesc: String
    val themeLight: String
    val themeLightDesc: String
    val themeDark: String
    val themeDarkDesc: String
    val languageSection: String
    val languageLabel: String
    val languageEnglish: String
    val languageTurkish: String
    val languageSystem: String
    val aboutSection: String
    val aboutDesc: String
    val storageInfo: String
    val storageInfoDesc: String
    val currentTheme: String
    val currentLanguage: String

    // Data Backup & Restore (JSON)
    val dataBackupSection: String
    val dataBackupDesc: String
    val exportJson: String
    val exportJsonDesc: String
    val exportSaveFile: String
    val exportCopyJson: String
    val importJson: String
    val importJsonDesc: String
    val importOpenFile: String
    val importPasteJson: String
    val importDialogTitle: String
    val importPreviewSummary: String
    val importTasksFound: String
    val importStepsFound: String
    val importCategoriesFound: String
    val importModeLabel: String
    val importModeMerge: String
    val importModeMergeDesc: String
    val importModeReplace: String
    val importModeReplaceDesc: String
    val importConfirmButton: String
    val importSuccess: String
    val importFailed: String
    val exportSuccess: String
    val jsonCopied: String
    val pasteJsonDialogTitle: String
    val pasteJsonPlaceholder: String
    val previewJson: String
    val invalidJsonError: String
}

object EnglishStrings : AppStrings {
    override val appName = "Task Management"
    override val appSubtitle = "Step-by-Step Task & Procedure Guides"
    override val searchPlaceholder = "Search"
    override val clearSearch = "Clear search"

    override val overallProgress = "OVERALL PROGRESS"
    override val totalTasks = "Total Tasks"
    override val completed = "Completed"
    override val active = "In Progress"

    override val filterAll = "All"
    override val filterActive = "Active"
    override val filterDone = "Done"

    override val sortBy = "Sort"
    override val sortNewest = "Newest First"
    override val sortProgress = "Highest Progress"

    override val categoriesSection = "CATEGORIES"
    override val manageCategories = "Manage Categories"
    override val manageCategoriesDesc = "Add, edit, or remove task categories"
    override val addCategory = "Add Category"
    override val newCategoryPlaceholder = "Enter new category name..."
    override val renameCategory = "Rename Category"
    override val deleteCategory = "Delete Category"
    override val deleteCategoryConfirmTitle = "Delete Category?"
    override val deleteCategoryConfirmMsg = "Tasks under this category will be reassigned to 'General'."
    override val categoryAlreadyExists = "A category with this name already exists."
    override val categoryCannotBeEmpty = "Category name cannot be empty."
    override val allCategories = "All Categories"
    override val selectCategory = "Select Category"
    override val categoryLabel = "Category"
    override val customCategoryLabel = "Custom Category Name"
    override val noCategoriesFound = "No categories found"
    override val categoryCount = "Categories"

    override val newTask = "New Task"
    override val blueprints = "Blueprints"
    override val startGuide = "Start Guide"
    override val reviewSteps = "Review Steps"
    override val markDone = "Mark Done"
    override val done = "Done"
    override val reopen = "Reopen"
    override val cancel = "Cancel"
    override val saveChanges = "Save Changes"
    override val createTask = "Create Task"
    override val editTask = "Edit Task"
    override val duplicateTask = "Duplicate Task"
    override val resetProgress = "Reset Progress"
    override val deleteTask = "Delete Task"
    override val deleteConfirmTitle = "Delete Task?"
    override val deleteConfirmMsg = "This will permanently delete this task and all of its steps."
    override val confirmDelete = "Delete"
    override val save = "Save"
    override val edit = "Edit"

    override val noTasksTitle = "No tasks created yet"
    override val noTasksDesc = "Create a custom task with detailed 'What to do' and 'How to do it' steps, or pick a starter blueprint."
    override val noMatchingTasks = "No matching tasks found"
    override val noCompletedTasks = "No completed tasks yet"
    override val allTasksCompleted = "All tasks are completed!"

    override val taskOverview = "Task Overview"
    override val stepCompletion = "Step Completion"
    override val procedureStepsHeader = "PROCEDURE STEPS"
    override val tapToToggleOrFocus = "Tap to toggle or start guide"
    override val noStepsAdded = "No steps added yet."
    override val addSteps = "Add Steps"

    override val stepOf = "Step"
    override val stepTimer = "Step Timer"
    override val prevStep = "Prev"
    override val nextStep = "Next Step"
    override val completeAndNext = "Complete & Next"
    override val completeAndFinish = "Complete & Finish"
    override val allDone = "All Done"
    override val whatToDo = "WHAT TO DO"
    override val howToDoIt = "HOW TO DO IT"
    override val proTipHeader = "PRO-TIP / KEY CHECKPOINT"
    override val whatToDoHeader = "WHAT TO DO"
    override val howToDoItHeader = "HOW TO DO IT"
    override val tipsAndAdviceHeader = "PRO-TIP / KEY CHECKPOINT"
    override val procedureCompleted = "Procedure Completed!"
    override val procedureCompletedDesc = "You finished all steps for this task."
    override val exitGuide = "Exit Guide"

    override val createTaskHeader = "Create Task & Procedure"
    override val editTaskHeader = "Edit Task & Procedure"
    override val taskDetailsHeader = "TASK DETAILS"
    override val taskTitleLabel = "Task Title *"
    override val taskTitlePlaceholder = "e.g. Prepare Client Strategy Deck"
    override val goalContextLabel = "Goal / Context (Optional)"
    override val goalContextPlaceholder = "Why this task matters, target outcome, requirements..."
    override val stepProcedureHeader = "STEP-BY-STEP PROCEDURE"
    override val stepProcedureDesc = "Detail what to do and how to do it for each step"
    override val stepWhatLabel = "What to do *"
    override val stepWhatPlaceholder = "e.g. Define the core proposition"
    override val stepHowLabel = "How to do it (Detailed instructions)"
    override val stepHowPlaceholder = "Step-by-step instructions, method, checklist items, tools to open..."
    override val stepTipsLabel = "Tip / Key Checkpoint (Optional)"
    override val stepTipsPlaceholder = "e.g. Keep slides under 3 bullet points"
    override val addAnotherStep = "Add Another Step"
    override val addCheckpoint = "Add Checkpoint"
    override val addPrepStep = "Add Prep Step"
    override val moveUp = "Move Up"
    override val moveDown = "Move Down"
    override val removeStep = "Delete Step"
    override val pleaseEnterTitle = "Please enter a task title."
    override val pleaseAddStep = "Please add at least 1 step with 'What to do'."

    override val settingsTitle = "Settings"
    override val appearanceSection = "APPEARANCE & THEME"
    override val themeModeLabel = "App Theme"
    override val themeSystem = "System Default"
    override val themeSystemDesc = "Matches your device display mode"
    override val themeLight = "Light Theme"
    override val themeLightDesc = "Clean and bright high-contrast theme"
    override val themeDark = "Dark Theme"
    override val themeDarkDesc = "Comfortable low-light dark theme"
    override val languageSection = "LANGUAGE / DİL"
    override val languageLabel = "App Language"
    override val languageEnglish = "English"
    override val languageTurkish = "Türkçe (Turkish)"
    override val languageSystem = "System Language"
    override val aboutSection = "ABOUT & STORAGE"
    override val aboutDesc = "Task Management application with step-by-step procedure guides and offline storage."
    override val storageInfo = "Data Storage Location"
    override val storageInfoDesc = "All your tasks and steps are stored 100% locally on your device in Room SQLite database. No external servers or cloud accounts required."
    override val currentTheme = "Current Theme"
    override val currentLanguage = "Selected Language"

    // Data Backup & Restore (JSON)
    override val dataBackupSection = "DATA BACKUP & RESTORE (JSON)"
    override val dataBackupDesc = "Export your tasks to a JSON file to save backups or transfer to another device. Import JSON backups anytime."
    override val exportJson = "Export to JSON"
    override val exportJsonDesc = "Save tasks, steps, and categories to a JSON backup file"
    override val exportSaveFile = "Save JSON File"
    override val exportCopyJson = "Copy / Share JSON"
    override val importJson = "Import from JSON"
    override val importJsonDesc = "Restore tasks and categories from a JSON backup file"
    override val importOpenFile = "Select JSON File"
    override val importPasteJson = "Paste JSON Text"
    override val importDialogTitle = "Import JSON Backup"
    override val importPreviewSummary = "Backup Preview"
    override val importTasksFound = "Tasks found"
    override val importStepsFound = "Steps found"
    override val importCategoriesFound = "Categories found"
    override val importModeLabel = "Select Import Mode"
    override val importModeMerge = "Merge with Existing"
    override val importModeMergeDesc = "Keep current tasks and add imported tasks and categories"
    override val importModeReplace = "Replace Everything"
    override val importModeReplaceDesc = "Clear all current tasks and restore completely from backup"
    override val importConfirmButton = "Import Backup"
    override val importSuccess = "Backup imported successfully!"
    override val importFailed = "Import failed: "
    override val exportSuccess = "JSON backup exported successfully!"
    override val jsonCopied = "JSON copied to clipboard!"
    override val pasteJsonDialogTitle = "Paste JSON Backup"
    override val pasteJsonPlaceholder = "Paste your JSON backup text here..."
    override val previewJson = "Preview & Import"
    override val invalidJsonError = "Invalid JSON format. Please check the text or file."
}

object TurkishStrings : AppStrings {
    override val appName = "Görev Yönetimi"
    override val appSubtitle = "Adım Adım Görev ve Talimat Rehberi"
    override val searchPlaceholder = "Search"
    override val clearSearch = "Aramayı temizle"

    override val overallProgress = "GENEL İLERLEME"
    override val totalTasks = "Toplam Görev"
    override val completed = "Tamamlanan"
    override val active = "Devam Eden"

    override val filterAll = "Hepsi"
    override val filterActive = "Aktif"
    override val filterDone = "Bitenler"

    override val sortBy = "Sırala"
    override val sortNewest = "En Yeni"
    override val sortProgress = "En Çok İlerleyen"

    override val categoriesSection = "KATEGORİLER"
    override val manageCategories = "Kategorileri Yönet"
    override val manageCategoriesDesc = "Özel görev kategorilerinizi ekleyin, düzenleyin veya silin"
    override val addCategory = "Kategori Ekle"
    override val newCategoryPlaceholder = "Yeni kategori adı girin..."
    override val renameCategory = "Kategoriyi Yeniden Adlandır"
    override val deleteCategory = "Kategoriyi Sil"
    override val deleteCategoryConfirmTitle = "Kategori Silinsin mi?"
    override val deleteCategoryConfirmMsg = "Bu kategorideki görevler 'Genel' kategorisine aktarılacaktır."
    override val categoryAlreadyExists = "Bu isimde bir kategori zaten mevcut."
    override val categoryCannotBeEmpty = "Kategori adı boş bırakılamaz."
    override val allCategories = "Tüm Kategoriler"
    override val selectCategory = "Kategori Seçin"
    override val categoryLabel = "Kategori"
    override val customCategoryLabel = "Özel Kategori Adı"
    override val noCategoriesFound = "Kategori bulunamadı"
    override val categoryCount = "Kategori"

    override val newTask = "Yeni Görev"
    override val blueprints = "Şablonlar"
    override val startGuide = "Rehberi Başlat"
    override val reviewSteps = "Adımları İncele"
    override val markDone = "Tamamla"
    override val done = "Bitti"
    override val reopen = "Yeniden Aç"
    override val cancel = "İptal"
    override val saveChanges = "Değişiklikleri Kaydet"
    override val createTask = "Görevi Oluştur"
    override val editTask = "Görevi Düzenle"
    override val duplicateTask = "Kopyasını Çıkar"
    override val resetProgress = "İlerlemeyi Sıfırla"
    override val deleteTask = "Görevi Sil"
    override val deleteConfirmTitle = "Görev Silinsin mi?"
    override val deleteConfirmMsg = "Bu işlem bu görevi ve bağlı tüm adımlarını kalıcı olarak silecektir."
    override val confirmDelete = "Sil"
    override val save = "Kaydet"
    override val edit = "Düzenle"

    override val noTasksTitle = "Henüz görev oluşturulmadı"
    override val noTasksDesc = "'Ne Yapılacak' ve 'Nasıl Yapılacak' adımlarını içeren yeni bir görev oluşturun veya hazır şablonları kullanın."
    override val noMatchingTasks = "Eşleşen görev bulunamadı"
    override val noCompletedTasks = "Henüz tamamlanan görev yok"
    override val allTasksCompleted = "Tüm görevler tamamlandı!"

    override val taskOverview = "Görev Özeti"
    override val stepCompletion = "Adım Tamamlama"
    override val procedureStepsHeader = "GÖREV ADIMLARI"
    override val tapToToggleOrFocus = "Tamamlamak veya rehbere geçmek için dokunun"
    override val noStepsAdded = "Henüz adım eklenmedi."
    override val addSteps = "Adım Ekle"

    override val stepOf = "Adım"
    override val stepTimer = "Adım Kronometresi"
    override val prevStep = "Önceki"
    override val nextStep = "Sonraki Adım"
    override val completeAndNext = "Tamamla ve Geç"
    override val completeAndFinish = "Tamamla ve Bitir"
    override val allDone = "Tümü Bitti"
    override val whatToDo = "NE YAPILACAK"
    override val howToDoIt = "NASIL YAPILACAK"
    override val proTipHeader = "İPUCU / KONTROL NOKTASI"
    override val whatToDoHeader = "NE YAPILACAK"
    override val howToDoItHeader = "NASIL YAPILACAK"
    override val tipsAndAdviceHeader = "İPUCU / KONTROL NOKTASI"
    override val procedureCompleted = "Görev Tamamlandı!"
    override val procedureCompletedDesc = "Bu göreve ait tüm adımları başarıyla bitirdiniz."
    override val exitGuide = "Rehberden Çık"

    override val createTaskHeader = "Görev ve Talimat Oluştur"
    override val editTaskHeader = "Görevi Düzenle"
    override val taskDetailsHeader = "GÖREV DETAYLARI"
    override val taskTitleLabel = "Görev Başlığı *"
    override val taskTitlePlaceholder = "Örn: Müşteri Sunumunu Hazırla"
    override val goalContextLabel = "Hedef / Açıklama (İsteğe Bağlı)"
    override val goalContextPlaceholder = "Bu görevin amacı, beklenen sonuç veya notlar..."
    override val stepProcedureHeader = "ADIM ADIM TALİMATLAR"
    override val stepProcedureDesc = "Her adım için ne yapılacağını ve nasıl yapılacağını yazın"
    override val stepWhatLabel = "Ne yapılacak *"
    override val stepWhatPlaceholder = "Örn: Ana fikri ve sunum yapısını belirle"
    override val stepHowLabel = "Nasıl yapılacak (Detaylı Talimatlar)"
    override val stepHowPlaceholder = "Adım adım yöntem, açılacak araçlar, kontrol edilecek maddeler..."
    override val stepTipsLabel = "İpucu / Önemli Not (İsteğe Bağlı)"
    override val stepTipsPlaceholder = "Örn: Slayt başına 3 maddeden fazla yazmayın"
    override val addAnotherStep = "Yeni Adım Ekle"
    override val addCheckpoint = "Kontrol Adımı Ekle"
    override val addPrepStep = "Hazırlık Adımı Ekle"
    override val moveUp = "Yukarı Taşı"
    override val moveDown = "Aşağı Taşı"
    override val removeStep = "Adımı Sil"
    override val pleaseEnterTitle = "Lütfen görev başlığını girin."
    override val pleaseAddStep = "Lütfen en az 1 adım ('Ne yapılacak') ekleyin."

    override val settingsTitle = "Ayarlar"
    override val appearanceSection = "GÖRÜNÜM VE TEMA"
    override val themeModeLabel = "Uygulama Teması"
    override val themeSystem = "Sistem Varsayılanı"
    override val themeSystemDesc = "Cihazınızın açık/koyu ekran moduna uyar"
    override val themeLight = "Açık Tema"
    override val themeLightDesc = "Ferah ve aydınlık tema"
    override val themeDark = "Karanlık Tema"
    override val themeDarkDesc = "Gözü yormayan koyu tema"
    override val languageSection = "DİL SEÇİMİ / LANGUAGE"
    override val languageLabel = "Uygulama Dili"
    override val languageEnglish = "English (İngilizce)"
    override val languageTurkish = "Türkçe"
    override val languageSystem = "Sistem Dili"
    override val aboutSection = "HAKKINDA VE DEPOLAMA"
    override val aboutDesc = "Adım adım talimatlar ve yerel depolama özellikli Görev Yönetimi uygulaması."
    override val storageInfo = "Veri Depolama Konumu"
    override val storageInfoDesc = "Tüm görev ve adımlarınız %100 yerel olarak cihazınızda Room SQLite veritabanında saklanır. Hiçbir harici sunucuya iletilmez."
    override val currentTheme = "Seçili Tema"
    override val currentLanguage = "Seçili Dil"

    // Data Backup & Restore (JSON)
    override val dataBackupSection = "VERİ YEDEKLEME VE GERİ YÜKLEME (JSON)"
    override val dataBackupDesc = "Uygulama silinse bile verilerinizi kaybetmemek için tüm görevlerinizi JSON dosyası olarak yedekleyebilir veya geri yükleyebilirsiniz."
    override val exportJson = "JSON Olarak Dışa Aktar"
    override val exportJsonDesc = "Görevleri, adımları ve kategorileri JSON yedek dosyasına kaydet"
    override val exportSaveFile = "JSON Dosyasını Kaydet"
    override val exportCopyJson = "JSON Kopyala / Paylaş"
    override val importJson = "JSON'dan İçe Aktar"
    override val importJsonDesc = "JSON yedek dosyasından görevleri ve kategorileri geri yükle"
    override val importOpenFile = "JSON Dosyası Seç"
    override val importPasteJson = "JSON Metni Yapıştır"
    override val importDialogTitle = "JSON Yedeğini İçe Aktar"
    override val importPreviewSummary = "Yedek Özeti"
    override val importTasksFound = "Bulunan görev"
    override val importStepsFound = "Bulunan adım"
    override val importCategoriesFound = "Bulunan kategori"
    override val importModeLabel = "İçe Aktarma Modu Seçin"
    override val importModeMerge = "Mevcut Verilerle Birleştir"
    override val importModeMergeDesc = "Mevcut görevleri korur, yedekteki görev ve kategorileri ekler"
    override val importModeReplace = "Tümünü Değiştir (Sıfırdan Geri Yükle)"
    override val importModeReplaceDesc = "Mevcut tüm görevleri siler ve tamamen yedekten geri yükler"
    override val importConfirmButton = "Yedeği İçe Aktar"
    override val importSuccess = "Yedek başarıyla içe aktarıldı!"
    override val importFailed = "İçe aktarma başarısız: "
    override val exportSuccess = "JSON yedeği başarıyla dışa aktarıldı!"
    override val jsonCopied = "JSON panoya kopyalandı!"
    override val pasteJsonDialogTitle = "JSON Yedeği Yapıştır"
    override val pasteJsonPlaceholder = "JSON yedek kodunuzu buraya yapıştırın..."
    override val previewJson = "İncele ve İçe Aktar"
    override val invalidJsonError = "Geçersiz JSON formatı. Lütfen dosya veya metin içeriğini kontrol edin."
}
