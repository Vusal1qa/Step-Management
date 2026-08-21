package com.example.data.model

data class StepTemplate(
    val whatToDo: String,
    val howToDoIt: String,
    val tipsOrAdvice: String = ""
)

data class TaskTemplate(
    val title: String,
    val description: String,
    val category: String,
    val iconName: String,
    val steps: List<StepTemplate>
)

object PredefinedTemplates {
    val templates = listOf(
        TaskTemplate(
            title = "Write a Compelling Technical Article",
            description = "Plan, outline, write, and publish an insightful tutorial or engineering post.",
            category = "Writing",
            iconName = "article",
            steps = listOf(
                StepTemplate(
                    whatToDo = "Define Target Reader & Concrete Learning Outcome",
                    howToDoIt = "Answer: Who is reading this? What will they be able to build or solve in 10 minutes after reading? Write a 1-sentence promise.",
                    tipsOrAdvice = "Be specific: 'How to build X with Y' is better than 'Intro to Y'."
                ),
                StepTemplate(
                    whatToDo = "Create Code Snippets & Verify Working Repository",
                    howToDoIt = "Build the minimal runnable code example. Run it from clean state to ensure no hidden dependencies or missing imports.",
                    tipsOrAdvice = "Readers will copy-paste your code; make sure it compiles flawlessly."
                ),
                StepTemplate(
                    whatToDo = "Write the Step-by-Step Walkthrough with Visuals",
                    howToDoIt = "Break down each code section. Explain WHY certain decisions were made, not just syntax explanations. Add annotated screenshots or diagrams.",
                    tipsOrAdvice = "Use active voice and direct instructions."
                ),
                StepTemplate(
                    whatToDo = "Proofread for Readability & Add Summary Checklist",
                    howToDoIt = "Read aloud once. Format headers (H2, H3) consistently. Add a 'Key Takeaways' bullet list at the end.",
                    tipsOrAdvice = "Check for clarity and formatting on mobile view."
                )
            )
        ),
        TaskTemplate(
            title = "Launch a New Project Sprint / Feature Kickoff",
            description = "Systematic checklist to kick off a feature with clear requirements and milestone targets.",
            category = "Work",
            iconName = "rocket_launch",
            steps = listOf(
                StepTemplate(
                    whatToDo = "Define Success Metrics & Scope Boundaries",
                    howToDoIt = "Document the problem statement, primary user persona, non-negotiable requirements, and what is explicitly OUT of scope.",
                    tipsOrAdvice = "Defining what NOT to build is as critical as defining what to build."
                ),
                StepTemplate(
                    whatToDo = "Break Down Technical Architecture & Data Models",
                    howToDoIt = "List all entities, API contracts, dependencies, and state management flows needed across frontend and backend.",
                    tipsOrAdvice = "Sketch a quick sequence or ER diagram before writing code."
                ),
                StepTemplate(
                    whatToDo = "Decompose into Bite-Sized Tickets (Sub-tasks)",
                    howToDoIt = "Create tasks where each ticket can be finished and reviewed within 1 to 2 days maximum.",
                    tipsOrAdvice = "Assign clear acceptance criteria to each ticket."
                ),
                StepTemplate(
                    whatToDo = "Align Team & Set Review Milestones",
                    howToDoIt = "Hold a 15-minute kickoff sync. Clarify blockers, code review expectations, and target demo dates.",
                    tipsOrAdvice = "Ensure everyone has repository permissions and sandbox credentials."
                )
            )
        ),
        TaskTemplate(
            title = "Focused Study Session (Feynman Technique)",
            description = "Master a complex topic by breaking it down and teaching it simply.",
            category = "Study",
            iconName = "school",
            steps = listOf(
                StepTemplate(
                    whatToDo = "Pick Target Concept & Read Foundational Material",
                    howToDoIt = "Set a 20-minute timer. Read textbook chapters or core documentation with complete focus. Do not multitask.",
                    tipsOrAdvice = "Highlight only fundamental rules and mechanisms."
                ),
                StepTemplate(
                    whatToDo = "Explain the Concept as if Teaching a 10-Year-Old",
                    howToDoIt = "Take a blank sheet of paper. Write an explanation using plain, everyday language and simple analogies. Avoid complex terminology.",
                    tipsOrAdvice = "If you need jargon to explain it, you don't fully understand it yet."
                ),
                StepTemplate(
                    whatToDo = "Identify Knowledge Gaps & Revisit Source",
                    howToDoIt = "Notice where you hesitated or used hand-wavy explanations. Re-read the source material specifically targeting those weak spots.",
                    tipsOrAdvice = "Focus your study energy exclusively on these friction points."
                ),
                StepTemplate(
                    whatToDo = "Create a 1-Page Summary Cheat Sheet",
                    howToDoIt = "Synthesize key formulas, diagrams, and summary insights onto a single reference card.",
                    tipsOrAdvice = "Test yourself on it 24 hours later for active recall."
                )
            )
        ),
        TaskTemplate(
            title = "Home Meal Prep & Kitchen Batch Cooking",
            description = "Efficient plan to prepare healthy meals for the week with minimal cleanup.",
            category = "Life & Home",
            iconName = "restaurant",
            steps = listOf(
                StepTemplate(
                    whatToDo = "Inventory Ingredients & Mise en Place (Prep Work)",
                    howToDoIt = "Wash, peel, and chop all vegetables. Measure spices and sauces into prep bowls before turning on any stove.",
                    tipsOrAdvice = "Having everything pre-chopped cuts cooking stress by 80%."
                ),
                StepTemplate(
                    whatToDo = "Start Long-Cook Proteins & Grains First",
                    howToDoIt = "Preheat oven to 400°F (200°C). Season chicken/tofu/fish and roast on sheet pans. Start rice, quinoa, or pasta on the stovetop.",
                    tipsOrAdvice = "Use parchment paper on baking sheets for zero-effort cleanup."
                ),
                StepTemplate(
                    whatToDo = "Sauté Greens & Prepare Dressings/Sauces",
                    howToDoIt = "Cook quick-steaming vegetables like broccoli or spinach in a skillet. Whisk dressings in small airtight jars.",
                    tipsOrAdvice = "Store dressings separately from greens so salads stay crisp."
                ),
                StepTemplate(
                    whatToDo = "Portion into Glass Containers & Clean As You Go",
                    howToDoIt = "Divide proteins, carbs, and veggies evenly into 4-5 meal prep containers. Let cool slightly, label dates, and refrigerate.",
                    tipsOrAdvice = "Wash cutting boards and pans while food is cooling."
                )
            )
        )
    )
}
