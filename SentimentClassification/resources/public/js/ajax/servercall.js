/**
 * Created by Jesse on 1/24/2017.
 */
console.log("----servercall.js ---");

var word_list = [
    {text: "Lorem", weight: 13},
    {text: "Ipsum", weight: 10.5},
    {text: "Dolor", weight: 9.4},
    {text: "Sit", weight: 8},
    {text: "Amet", weight: 6.2},
    {text: "Consectetur", weight: 5},
    {text: "Adipiscing", weight: 5},
    {text: "Elit", weight: 5},
    {text: "Nam et", weight: 5},
    {text: "Leo", weight: 4},
    {text: "Sapien", weight: 4},
    {text: "Pellentesque", weight: 3},
    {text: "habitant", weight: 3},
    {text: "morbi", weight: 3},
    {text: "tristisque", weight: 3},
    {text: "senectus", weight: 3},
    {text: "et netus", weight: 3},
    {text: "et malesuada", weight: 3},
    {text: "fames", weight: 2},
    {text: "ac turpis", weight: 2},
    {text: "egestas", weight: 2},
    {text: "Aenean", weight: 2},
    {text: "vestibulum", weight: 2},
    {text: "elit", weight: 2},
    {text: "sit amet", weight: 2},
    {text: "metus", weight: 2},
    {text: "adipiscing", weight: 2},
    {text: "ut ultrices", weight: 2},
    {text: "justo", weight: 1},
    {text: "dictum", weight: 1},
    {text: "Ut et leo", weight: 1},
    {text: "metus", weight: 1},
    {text: "at molestie", weight: 1},
    {text: "purus", weight: 1},
    {text: "Curabitur", weight: 1},
    {text: "diam", weight: 1},
    {text: "dui", weight: 1},
    {text: "ullamcorper", weight: 1},
    {text: "id vuluptate ut", weight: 1},
    {text: "mattis", weight: 1},
    {text: "et nulla", weight: 1},
    {text: "Sed", weight: 1}
];


$('button .glyphicon.glyphicon-search').click(function() {
    var query= $('#custom-search-input input.form-control.input-lg').val();
    if(query.trim()===""){
        $('h2.sub-header').html("<h3>Please enter a valid string.</h3>");
        return;
    }
    var method= $('#methods').val();
    $('h2.sub-header').html(" <h3>Please wait for \""+ query+ "\" Twitter Sentiment score. <BR> This could take several seconds...<h3>" );
    $.ajax({
       url: '/score',
        data: {"tweetSubject": query,"method": method},
        dataType: 'json',
        success: function(data) {
        	console.log(data);
            //$('h2.sub-header').text("Net Promoter Score : " + data["score"]);
            $('h2.sub-header').html(query + "</br> Net Promoter Score : " + data["score"]);
            $("#my_favorite_latin_words").html("");
            //$("#my_favorite_latin_words").jQCloud(word_list);
            $("#my_favorite_latin_words").jQCloud(data["topWords"]);    
            drawChart(query, method, data);


      },
      error: function() {
         $('h2.sub-header').text('An error occurred');
      },
       type: 'GET'
   });
});

function drawChart(query, method,  data){
    var total = data.posCount + data.negCount + data.neutralCount
    var posPercent = data.posCount / total * 100.0
    var negPercent = data.negCount /total * 100.0
    var neutralCount = data.neutralCount /total * 100.0

    Highcharts.chart('chart', {
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
    },
    title: {
        text: 'Sentiment Results for query \"'+query+'\"'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            }
        }
    },
    series: [{
        name: 'Sentiment',
        colorByPoint: true,
        data: [{
            name: 'Positive',
            y: posPercent,
            sliced: true,
            selected: true
        }, {
            name: 'Negative',
            y: negPercent
        }, {
            name: 'Neutral',
            y: neutralCount
        }]
    }]
});
}