# Derived contract metrics
#
# This helper is intentionally defined without package dependencies so that
# `source("R/derive.R")` always provides `derive_all()` in the calling
# environment.
derive_all <- function(df) {
  if (!is.data.frame(df)) {
    stop("df must be a data.frame")
  }

  required_cols <- c(
    "ApprovedBudgetForContract",
    "ContractCost",
    "StartDate",
    "ActualCompletionDate"
  )

  missing_cols <- setdiff(required_cols, names(df))
  if (length(missing_cols) > 0) {
    stop(sprintf(
      "df is missing required columns: %s",
      paste(missing_cols, collapse = ", ")
    ))
  }

  # Guardrails for extreme numeric inputs
  budget_guard <- !is.na(df$ApprovedBudgetForContract) &
    abs(df$ApprovedBudgetForContract) > 1e12
  cost_guard <- !is.na(df$ContractCost) & abs(df$ContractCost) > 1e12
  replaced_rows <- budget_guard | cost_guard

  if (any(budget_guard)) {
    df$ApprovedBudgetForContract[budget_guard] <- NA_real_
  }
  if (any(cost_guard)) {
    df$ContractCost[cost_guard] <- NA_real_
  }
  if (any(replaced_rows)) {
    message(
      sprintf(
        "derive_all: replaced guardrail values in %d row(s)",
        sum(replaced_rows)
      )
    )
  }

  df$CostSavings <- df$ApprovedBudgetForContract - df$ContractCost
  df$CompletionDelayDays <- as.numeric(df$ActualCompletionDate - df$StartDate)

  df
}

# Ensure the helper is always available to callers even when sourced with
# `local = TRUE` by copying the definition into the global environment.
assign("derive_all", derive_all, envir = .GlobalEnv)
