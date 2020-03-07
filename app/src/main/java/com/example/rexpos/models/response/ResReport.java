package com.example.rexpos.models.response;

import com.example.rexpos.models.ReportItem;
import com.example.rexpos.models.Transaction;

import java.util.List;

public class ResReport extends ResBase {

    public Result results = null;

    public class Result{
        public List<ReportItem> reports = null;
    }
}
