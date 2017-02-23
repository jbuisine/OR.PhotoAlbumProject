/**
 * Created by jbuisine on 09/02/17.
 */

//Get all files of current template type
$( "#albumType" ).change(function() {

    $.ajax({
        type: "POST",
        url: '/load-solutions',
        data: {
            albumType: $("#albumType").val(),
            templateName: $("#templateName").val()
        },
        success: loadSelectSol
    });
});

function loadSelectSol(solutions) {
    $( "#solutionFile" ).empty();

    if(solutions.length > 0){
        $("#solutionFile").attr();
        $.each(solutions, function( key, sol ) {
            $("#solutionFile").append('<option value="' + sol + '">' + sol + '</option>');
        });
    }
    else{
        $("#solutionFile").append('<option> No solution found</option>');
    }
}

$("#solutionFile").change(function () {
    $.ajax({
        type: "POST",
        url: '/load-solution-content',
        data: {
            templateName: $("#templateName").val(),
            albumType: $("#albumType").val(),
            solutionFile: $(this).val()
        },
        success: loadContentSol
    });
});

function loadContentSol(data) {
    var head = data[0];
    data.splice(0, 1);

    switch (head.length){
        case 2 :
            var xAxis = [];
            var yAxis = [];

            data.forEach(function (elem) {
                xAxis.push(elem[1]);
                yAxis.push(elem[2]);
            });

            var line = {
                x: xAxis,
                y: yAxis,
                type: 'scatter',
                mode: 'markers'
            };

            var layout = {
                title: 'Solution file name',
                xaxis: {
                    title: head[0],
                    type: 'log',
                    autorange: true,
                    titlefont: {
                        family: 'Courier New, monospace',
                        size: 18,
                        color: '#7f7f7f'
                    }
                },
                yaxis: {
                    title: head[1],
                    type: 'log',
                    autorange: true,
                    titlefont: {
                        family: 'Courier New, monospace',
                        size: 18,
                        color: '#7f7f7f'
                    }
                }
            };

            Plotly.newPlot('graphic-representation', [line], layout);
            break;

        case 3 :

            var xAxis = [];
            var yAxis = [];
            var zAxis = [];

            data.forEach(function (elem) {
                xAxis.push(elem[1]);
                yAxis.push(elem[2]);
                zAxis.push(elem[3]);
            });

            var line1 = {
                type:'markers',
                x: xAxis,
                y: yAxis,
                z: zAxis,
                color: 'blue'
            };

            var layout = {
                scene: {
                    xaxis: {
                        title: head[0]
                    },
                    yaxis: {
                        title: head[1]
                    },
                    zaxis: {
                        title: head[2]
                    }
                },
                autosize: true
            };

            Plotly.plot('myDiv', [line1], layout);

            break;
    }

    $('#generation-info-modal').modal();
}