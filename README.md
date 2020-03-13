# Yahoo Finance Songbird Charts
>  Allows for other apps to render accessible audio charts

Charts such as stocks, sports scores and political polls are still almost impossible to use for people with visual impairment. The quick summary of thousands or more data points charts are meant for is virtually not usable for about 300 million people worldwide and for users on a voice interface. This is a first step towards changing that. 

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [Contribute](#contribute)
- [Maintainers](#maintainers)
- [License](#license)

## Background

This project embodies Verizon Media's commitment to accessibility and to going above and beyond in making our products accessible to all. What started as a conversation about the state of charts for users with visual impairment led to an initial prototype, several user stidies and finally, this solution presenting primarily visual information with music and haptics. We realize that this problem is bigger than financial graphs are also hoping to see where the open source community takes this approach and applies it to interfaces such as voice, web etc. The tones are played in Hz. This is largely a reference implementation and we'll try to review pull requests as we can. We are however not sure of the SLAs yet. Thank you! 

## Install
If you've never used git before, please take a moment to familiarize yourself with [what it is and how it works](https://git-scm.com/book/en/v2/Getting-Started-Git-Basics). To install this project, you'll [need to have git installed and set up](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) on your local dev environment.

## Usage

It's as easy as adding this view to your XML file:
>
    <com.yahoo.mobile.android.audiocharts.view.SongbirdChartView
        android:id="@+id/songbird_view"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

Initializing it with a `AudioChartViewModel` and a `ScrubCallback`:
>
      songbirdView.setViewModel(chartViewModel)

And act on that chart object to have it do whatever you need (more details below):
>
    songbirdView.playSummaryAudio()

Don't forget to dispose when you're done:
>
    songbirdView.dispose()

### SongbirdChartView
#### playSummaryAudio()
Plays each chart point's tone in succession

#### dispose()
Notifies SongbirdChartView to dispose of any disposables to avoid memory leaks

### AudioChartViewModel
#### val contentDescription: String
The summary content description for the chart view, not applied on it's own. You can use this in your own view wherever it fits best. The sample uses it in the toolbar.

#### val benchmark: Double
The value that represents the dotted benchmark line that goes horizontally across the chart

#### val chartDataPoints: List<AudioChartPointViewModel>
The chart data points

#### val priceHint: Int
Price hint for the price labels renderd in the y-axis (e.g. priceHint of 2.0 will render 100.00)

#### val showXAxis: Boolean = true
Displays x-axis with timestamps. True by default

#### val xAxisLabels: List<XAxisLabel> = listOf()
The XAxisLabels to be displayed if showXAxis is true

#### val showYAxis: Boolean = true
Displays y-axis with highestPointValue, lowestPointValue, benchmark value, and the last value in the list. True by default

#### @ColorRes val fillColor: Int
The color of the gradient below the chart line

#### @ColorRes val strokeColor: Int
The color of the chart line itself

#### @ColorRes val benchmarkColor: Int = R.color.chart_line
The color of the dotted benchmark line

### AudioChartPointViewModel
#### val value: Double
The value of this point

#### val timestamp: Long
The timestamp of this point in milliseconds

#### val index: Int
The index of this point in the list

#### val onSwipeListener: (() -> Unit)
To be notified whenever the user swiped onto this point and is now focused on it in talkback mode

### XAxisLabel
#### val index: Int
The index of this label in the list

#### val label: String
The timestamp converted into a string format to be displayed. Typically uses the AudioChartPointViewModel's timestamp when the index matches. It's up to you to format the timestamp to your needs.

### AudioRecyclerView.ScrubCallback
#### onScrub(x: Float)
The chart has received a motion event at this rawX (one finger drag when talkback is off, two finger drag when talkback is on)

#### onRelease(x: Float)
The chart was being scrubbed and has now been released at this rawX

### DimensionUtils
#### getItemIndexByWidth(context: Context, numItems: Int, x: Float): Int
`context`: Used to retrieve the screen width in pixels

`numItems`: the total number of chart points

`x`: rawX of the Touch MotionEvent

returns the closest index of `AudioChartPointViewModel` that maps to the rawX

## Contribute
Please refer to [the contributing.md file](Contributing.md) for information about how to get involved. We welcome issues, questions, and pull requests. Pull Requests are welcome.

## Maintainers
Finance Mobile Android finance-android-dev@verizonmedia.com

## License
This project is licensed under the terms of the [Apache 2.0](LICENSE-Apache-2.0) open source license. Please refer to [LICENSE](LICENSE) for the full terms.