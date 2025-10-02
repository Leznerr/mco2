derive_all <- function(df) {
  if (!is.data.frame(df)) {
    stop("df must be a data.frame")
  }

  required_cols <- c("ApprovedBudgetForContract", "ContractCost", "StartDate", "ActualCompletionDate")
  if (!all(required_cols %in% names(df))) {
    stop("df is missing required columns")
  }

  budget_guard <- !is.na(df$ApprovedBudgetForContract) & abs(df$ApprovedBudgetForContract) > 1e12
  cost_guard <- !is.na(df$ContractCost) & abs(df$ContractCost) > 1e12
  replaced_rows <- budget_guard | cost_guard

  if (any(budget_guard)) {
    df$ApprovedBudgetForContract[budget_guard] <- NA_real_
  }

  if (any(cost_guard)) {
    df$ContractCost[cost_guard] <- NA_real_
  }

  if (any(replaced_rows)) {
    message(sprintf("derive_all: replaced guardrail values in %d row(s)", sum(replaced_rows)))
  }

  df$CostSavings <- df$ApprovedBudgetForContract - df$ContractCost
  df$CompletionDelayDays <- as.numeric(df$ActualCompletionDate - df$StartDate)

  df
}
