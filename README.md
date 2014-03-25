#Iron Condor Back Testing
Back testing application to analyze the profitability of weekly Iron Condors using Z-Scores to set the short strike prices.

###Back Testing Hypothesis
The idea is to sell Iron Condors with high probability of success every week, making incremental gains consistently over time.

    Step 1: Calculate mean
    Step 2: Calculate standard deviation
    Step 3: Calculate short strike prices based on different Z-Scores
    Step 4: Test profitability over time

By calculating the short strikes for different Z-Scores, I can estimate a probability of success for each Iron Condor.  Next,
with the Black Scholes algorithm I wrote, I can calculate which Iron Condors will yield the highest credits and demonstrate
the highest volatility (there should be strong correlation between the two).  By selling the Iron Condors with the highest
credits every week, will I be profitable over time?  Which Z-score will yield the highest profit?

### Application Architecture
Overall design has many flaws and there are parts that I would do differently (better separation of concerns and more
emphasis on proven design patterns, proper use of Enums), but the flow is easy to follow and can be easily conveyed on a sequence diagram.

A 'Prepare*.java' has a '*DataLoader.java' which has a '*Helper.java'.

Each Prepare*.java file can be ran individually or the entire process can be ran from PrepareEnitireHistory.java.  Each Preparer
knows how to call the Thread Executor which divides the work up evenly based on the suggested amount threads for ech type.

Each DataLoader knows how to take a section work, collect the data from a given source (Web Crawler, Database, etc), and
execute Helper functions to process data before moving on to the next phase.

The Helpers do the heavy lifting for the DataLoaders by processing and calculating the data into a savable format.

### Tech Stack
Spring -> Java -> Morphia -> MongoDB

###Execution
    Step 1:  Go to yahoo download quote history
    Step 2:  Calculate Historical Volatility of stock
    Step 3:  Calculate Statistics
    Step 4:  Calculate short strike prices
    Step 5:  Calculate options price (Using Black Scholes Algorithm I wrote)
    Step 6:  Calculate P & L for weekly strategy
    Step 7:  Repeat for every week since Jan 1 2000
    Step 8:  Evaluate results.





