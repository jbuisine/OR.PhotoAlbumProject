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

    console.log(solutions.length);

    if(solutions.length > 0){
        $("#solutionFile").attr('disabled', false);
        $.each(solutions, function( key, sol ) {
            $("#solutionFile").append('<option value="' + sol + '">' + sol + '</option>');
        });
    }
    else{
        $("#solutionFile").attr('disabled', true);
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

        case 1:
            generate2DPlot(data, head);
            break;

        case 2 :
            generate2DPlot(data, head);
            break;

        case 3 :
            generate3DPlot(data, head);
            break;
    }

    $('#generation-info-modal').modal();
}

/**
 * Utility function used for generate 2D plot
 *
 * @param data
 * @param head
 */
function generate2DPlot(data, head){

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
}

/**
 *  Utility function used for generate 3D plot
 *
 * @param data
 * @param head
 */
function generate3DPlot(data, head){

    var xAxis = [];
    var yAxis = [];
    var zAxis = [];

    data.forEach(function (elem) {
        xAxis.push(elem[1]);
        yAxis.push(elem[2]);
        zAxis.push(elem[3]);
    });

    var line1 = {
        type: 'scatter3d',
        mode: 'markers',
        x: xAxis,
        y: yAxis,
        z: zAxis,
        marker: {
            color: 'rgb(23, 190, 207)',
            size: 2
        }
    };

    var layout = {
        aspectratio: {
            x: 1,
            y: 1,
            z: 1
        },
        camera: {
            center: {
                x: 0,
                y: 0,
                z: 0
            },
            eye: {
                x: 1,
                y: 1,
                z: 1
            },
            up: {
                x: 0,
                y: 0,
                z: 1
            }
        },
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
        height: 550,
        autosize: true,
        title: '3d point clustering'
    };

    Plotly.newPlot('graphic-representation', [line1], layout);
}