"use strict";

var origin = "http://localhost:8443";
var currentDay;
var currentWeek;
$(document).ready(function() {
    // Form selects default values
    $.ajax({
        url: origin + "/api/week",
        type: "GET",
        success: function success(response) {
            currentWeek = response.toString();
        },
        error: function error() {
            currentWeek = "0";
        }
    });
    $.ajax({
        url: origin + "/api/day",
        type: "GET",
        success: function success(response) {
            currentDay = response.toString();
        },
        error: function error() {
            currentDay = "0";
        }
    }); // Initial form view

    $("#v_room, #v_teacher").hide();
    $(document).ajaxComplete(function() {
        if (
            typeof currentWeek !== "undefined" &&
            typeof currentDay !== "undefined"
        ) {
            reset_selects();
            $(document).off("ajaxComplete");
            $(".s_week").change(function() {
                currentWeek = this.value;
            });
            $(".s_day").change(function() {
                currentDay = this.value;
            });
        }
    }); // Query selection buttons

    $("#b_group").click(function() {
        $("#f_group").trigger("reset");
        reset_selects();
        $("#v_group").show();
        $("#v_room, #v_teacher").hide();
    });
    $("#b_teacher").click(function() {
        $("#f_teacher").trigger("reset");
        reset_selects();
        $("#v_teacher").show();
        $("#v_room, #v_group").hide();
    });
    $("#b_room").click(function() {
        $("#f_room").trigger("reset");
        reset_selects();
        $("#v_room").show();
        $("#v_group, #v_teacher").hide();
    }); // Input autocompletion initialization

    assign_autocompletion("group", function(value) {
        return value.toUpperCase();
    });
    assign_autocompletion("teacher", function(value) {
        return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
    }); // Reassign form submission actions

    $("form").submit(function(e) {
        var form = $(this);
        if ($("#v_teacher").is(":visible"))
            if ($("#i_teacher").val().length < 8) {
                alert("Длина запроса слишком мала\nThe query is too short");
                return false;
            }
        if ($("#v_room").is(":visible"))
            if ($("#i_room").val().length < 3) {
                alert("Длина запроса слишком мала\nThe query is too short");
                return false;
            }
        $.ajax({
            url: origin + "/api/schedule",
            type: "GET",
            data: form.serialize(),
            success: fill_schedule
        });
        return false;
    });
}); // Utility functions

function reset_selects() {
    $(".s_week").val(currentWeek);
    $(".s_day").val(currentDay);
}

var groupBy = function groupBy(key) {
    return function(array) {
        return array.reduce(function(objectsByKeyValue, obj) {
            var value = obj[key];
            objectsByKeyValue[value] = (objectsByKeyValue[value] || []).concat(obj);
            return objectsByKeyValue;
        }, {});
    };
};

var groupByDay = groupBy("day");
var dayStrings = [
    "Понедельник",
    "Вторник",
    "Среда",
    "Четверг",
    "Пятница",
    "Суббота"
];

function fill_schedule(response) {
    // Clear existing contents
    $(".floating_list").empty(); // Group response by day

    var classes = Array.from(response.values());
    var classesWithDays = groupByDay(classes);
    $.each(classesWithDays, function(key, value) {
        // Add day header first
        $(".floating_list").append(
            jQuery("<div/>", {
                class: "header_day",
                text: dayStrings[parseInt(key) - 1]
            })
        ); // Time expansion for classes happening in one time period

        var i;
        var firstTimePresent = true;
        var pairList = value.sort(function(first, second) {
            if (
                parseInt(first["timeSpan"].split("-")[0], 10) <
                parseInt(second["timeSpan"].split("-")[0], 10)
            )
                return -1;
            else if (
                parseInt(first["timeSpan"].split("-")[0], 10) >
                parseInt(second["timeSpan"].split("-")[0], 10)
            )
                return 1;
            return 0;
        });

        for (i = 1; i < pairList.length; i++) {
            if (pairList[i - 1]["timeSpan"] == pairList[i]["timeSpan"]) {
                $(".floating_list").append(
                    make_record(
                        pairList[i - 1],
                        [firstTimePresent, false],
                        get_desc(pairList[i - 1])
                    )
                );
                firstTimePresent = false;
            } else {
                $(".floating_list").append(
                    make_record(
                        pairList[i - 1],
                        [firstTimePresent, true],
                        get_desc(pairList[i - 1])
                    )
                );
                $(".floating_list").append(jQuery("<hr/>"));
                firstTimePresent = true;
            }
        } // Finally append last element

        $(".floating_list").append(
            make_record(
                pairList[pairList.length - 1],
                [firstTimePresent, true],
                get_desc(pairList[pairList.length - 1])
            )
        );
        /*
        $.each(value, function (index, item) {
            $(".floating_list").append(make_record(item, [true, true]));
        });
         */
    });
}

function get_desc(item) {
    var desc;
    if ($("#v_teacher").is(":visible")) desc = item["group"];
    else if ($("#v_room").is(":visible"))
        desc = item["teacher"] + " (" + item["group"] + ")";
    else desc = item["teacher"];
    return desc;
}

function make_record(record, times, desc) {
    var main_holder = jQuery("<div/>", {
        class: "pair_item"
    });
    var container = jQuery("<div/>", {
        class: "pair_item_container"
    }); // Time column

    var time = record["timeSpan"].split("-");
    var time_holder = jQuery("<div/>", {
        class: "pair_item_time_column"
    });
    var time_start = jQuery("<div/>", {
        class: "pair_item_time",
        text: times[0] ? format_time(time[0]) : ""
    });
    var time_filler = jQuery("<div/>", {
        class: "pair_item_filler"
    });
    var time_end = jQuery("<div/>", {
        class: "pair_item_time",
        text: times[1] ? format_time(time[1]) : ""
    });
    time_holder.append(time_start, time_filler, time_end);
    container.append(time_holder); // Main column

    var main_column_holder = jQuery("<div/>", {
        class: "pair_item_main_column"
    }); // --- Token row

    var token_row = jQuery("<div/>", {
        class: "pair_item_token_row"
    });
    [
        record["one_half"],
        record["week"],
        record["over_week"] ? "ч/н" : "",
        record["type"]
    ]
        .filter(function(item) {
            return item;
        })
        .forEach(function(token) {
            var value = isNaN(token) ? token : "I".repeat(token);
            token_row.append(
                jQuery("<div/>", {
                    class: "pair_item_token",
                    text: value
                })
            );
        }); // --- Subject and teacher

    var subject = jQuery("<div/>", {
        class: "pair_item_subject",
        text: record["subject"]
    });
    var teacher = jQuery("<div/>", {
        class: "pair_item_teacher",
        text: desc
    });
    main_column_holder.append(token_row, subject, teacher);
    container.append(main_column_holder); // Room column

    var room_holder = jQuery("<div/>", {
        class: "pair_item_room_column"
    });
    var room_container = jQuery("<div>");
    room_container.css("text-align", "right"); //room_container.css('margin-top', '15px');

    room_container.css("margin-right", "3px");
    var rooms = record["room"].split(", ").filter(function(item) {
        return item != "Нет Аудитории";
    });
    rooms.forEach(function(item) {
        room_container.append(
            jQuery("<span/>", {
                text: item
            })
        );
        room_container.append(jQuery("<br/>"));
    }); // Building ID

    var building_view = jQuery("<span>", {
        class: "pair_item_token",
        text: "Уч. ц. " + record["buildingID"]
    });
    room_holder.append(room_container); // Building color

    building_view.css(
        "background-color",
        get_building_color(record["buildingID"])
    );
    building_view.css("float", "right"); // works?
    // Wrapping building token in div

    var building_wrapper = jQuery("<div/>", {
        overflow: "auto"
    });
    building_wrapper.append(building_view);
    room_holder.append(building_wrapper);
    container.append(room_holder);
    main_holder.append(container);
    return main_holder;
}

function get_building_color(buildingID) {
    switch (buildingID) {
        case 1:
            return "#428bf0";

        case 2:
            return "#ffce29";

        default:
            return "#23c15a";
    }
}

function format_time(time) {
    var withColon = time.replace(".", ":");
    return time.length < 5 ? "0" + withColon : withColon;
}

function assign_autocompletion(target, transformation) {
    var auto_options = {
        url: null,
        getValue: null,
        maxNumberOfElements: 3,
        requestDelay: 100,
        adjustWidth: false,
        cssClasses: ".group_form"
    };

    auto_options.url = function(value) {
        return (
            origin +
            "/api/autocomplete?type=" +
            target +
            "&value=" +
            transformation(value)
        );
    };

    auto_options.getValue = target;
    $("#i_" + target).easyAutocomplete(auto_options);
}