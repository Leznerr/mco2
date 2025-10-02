
derive_all <- function(df) {
  if (!is.data.frame(df)) {
    stop("df must be a data.frame")
  }


    stop("df is missing required columns")
  }

  budget_guard <- !is.na(df$ApprovedBudgetForContract) & abs(df$ApprovedBudgetForContract) > 1e12
  cost_guard <- !is.na(df$ContractCost) & abs(df$ContractCost) > 1e12



  df
}
