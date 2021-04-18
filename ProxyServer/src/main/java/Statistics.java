public class Statistics {
    private String domain;
    private long requestCount;
    private long requestTotal;
    private long responseTotal;

    public Statistics(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getRequestTotal() {
        return requestTotal;
    }

    public long getResponseTotal() {
        return responseTotal;
    }

    public void saveRequest(long requestSize, long responseSize) {
        this.requestCount += 1;
        this.requestTotal += requestSize;
        this.responseTotal += responseSize;
    }

    public void initExisting(long requestCount, long requestSize, long responseSize) {
        this.requestCount = requestCount;
        this.requestTotal = requestSize;
        this.responseTotal = responseSize;
    }

    public String getLine() {
        return String.format("%s,%d,%d,%d", this.getDomain(), this.getRequestCount(), this.getRequestTotal(), this.getResponseTotal());
    }


}
