/* Polish initialisation for the jQuery UI date picker plugin. */
/* Written by Jacek Wysocki (jacek.wysocki@gmail.com). */
jQuery(function($){
	$.datepicker.regional['pl'] = {clearText: 'Wyczy????????', clearStatus: 'Wyczy???????? obecn??
 dat????',
		closeText: 'Zamknij', closeStatus: 'Zamknij bez zapisywania',
		prevText: '&#x3c;Poprzedni', prevStatus: 'Poka???? poprzedni miesi??
c',
		nextText: 'Nast????pny&#x3e;', nextStatus: 'Poka???? nast????pny miesi??
c',
		currentText: 'Dzi????', currentStatus: 'Poka???? aktualny miesi??
c',
		monthNames: ['Stycze????','Luty','Marzec','Kwiecie????','Maj','Czerwiec',
		'Lipiec','Sierpie????','Wrzesie????','Pa????dziernik','Listopad','Grudzie????'],
		monthNamesShort: ['Sty','Lu','Mar','Kw','Maj','Cze',
		'Lip','Sie','Wrz','Pa','Lis','Gru'],
		monthStatus: 'Poka???? inny miesi??
c', yearStatus: 'Poka???? inny rok',
		weekHeader: 'Tydz', weekStatus: 'Tydzie???? roku',
		dayNames: ['Niedziela','Poniedzialek','Wtorek','????roda','Czwartek','Pi??
tek','Sobota'],
		dayNamesShort: ['Nie','Pn','Wt','????r','Czw','Pt','So'],
		dayNamesMin: ['N','Pn','Wt','????r','Cz','Pt','So'],
		dayStatus: 'Ustaw DD jako pierwszy dzie???? tygodnia', dateStatus: 'Wybierz D, M d',
		dateFormat: 'yy-mm-dd', firstDay: 1, 
		initStatus: 'Wybierz dat????', isRTL: false};
	$.datepicker.setDefaults($.datepicker.regional['pl']);
});
