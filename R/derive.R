library(dplyr)

derive_all <- function(df) {
  if (!is.data.frame(df)) {
    stop("df must be a data.frame")
  }

  if (!all(c("ApprovedBudgetForContract", "ContractCost", "StartDate", "ActualCompletionDate") %in% names(df))) {
    stop("df is missing required columns")
  }

  budget_guard <- !is.na(df$ApprovedBudgetForContract) & abs(df$ApprovedBudgetForContract) > 1e12
  cost_guard <- !is.na(df$ContractCost) & abs(df$ContractCost) > 1e12

  replaced_budget <- sum(budget_guard)
  replaced_cost <- sum(cost_guard)

  if (replaced_budget > 0) {
    df$ApprovedBudgetForContract[budget_guard] <- NA_real_
  }

  if (replaced_cost > 0) {
    df$ContractCost[cost_guard] <- NA_real_
  }

  if (replaced_budget > 0 || replaced_cost > 0) {
    message(
      sprintf(
        "derive_all: replaced %d ApprovedBudgetForContract value(s) and %d ContractCost value(s) exceeding guardrails",
        replaced_budget,
        replaced_cost
      )
    )
  }

  df <- df %>%
    mutate(
      CostSavings = ApprovedBudgetForContract - ContractCost,
      CompletionDelayDays = as.numeric(ActualCompletionDate - StartDate)
    )

  df
}
