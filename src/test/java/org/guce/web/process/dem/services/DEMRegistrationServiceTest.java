package org.guce.web.process.dem.services;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.dem.entities.DEMRegistration;
import org.guce.process.dem.repositories.impl.DEMRegistrationRepositoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DEMRegistrationServiceTest {
    protected static List<DEMRegistration> list;

    @Mock
    protected DEMRegistrationRepositoryImpl dEMRegistrationRepository;

    @Mock
    protected IdentifiantGeneratorLocal generator;

    @Mock
    protected CoreChargerFacadeLocal chargerFacade;

    @Mock
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @InjectMocks
    protected DEMRegistrationService instance;

    public DEMRegistrationServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            DEMRegistration c = new DEMRegistration();
            c.setRecordId(1 + "");
            c.setRecordState("true");
            c.setDemReference(nextString(17));
            c.setDemDate(GuceCalendarUtil.getCalendar().getTime());
            list.add(c);
        }
    }

    @Before
    public void setUp() {
        Mockito.when(dEMRegistrationRepository.count()).thenReturn(list.size());
        Mockito.when(dEMRegistrationRepository.findAll()).thenReturn(list);
        Mockito.when(dEMRegistrationRepository.search(ArgumentMatchers.any(SearchFilter.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(list);
        Mockito.when(dEMRegistrationRepository.searchCount(ArgumentMatchers.any(SearchFilter.class))).thenReturn(list.size());
    }

    @Test
    public void testCount() {
        System.out.println("count");
        int expResult = list.size();
        int result = instance.count();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindAll_0args() {
        System.out.println("findAll");
        List<DEMRegistration> expResult = list;
        List<DEMRegistration> result = instance.findAll();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSave_One() {
        System.out.println("save");
        DEMRegistration entity = list.get(0);
        entity.setRecordId("55");
        DEMRegistration expResult = entity;
        DEMRegistration result = instance.save(entity);
        Mockito.verify(dEMRegistrationRepository, Mockito.times(1)).edit(entity);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testRemove_One() {
        System.out.println("remove");
        DEMRegistration entity = list.get(0);
        instance.remove(entity);
        Mockito.verify(dEMRegistrationRepository, Mockito.times(1)).remove(entity);
    }

    @Test
    public void testRemove_List() {
        System.out.println("remove");
        instance.remove(list);
        Mockito.verify(dEMRegistrationRepository, Mockito.times(10)).remove(ArgumentMatchers.any(DEMRegistration.class));
    }

    @Test
    public void testSave_List() {
        System.out.println("save");
        instance.save(list);
        Mockito.verify(dEMRegistrationRepository, Mockito.times(10)).edit(ArgumentMatchers.any(DEMRegistration.class));
    }

    @Test
    public void testSearch() {
        System.out.println("search");
        SearchFilter filter = new SearchFilter();
        int start = 0;
        int count = 0;
        String orderField = "";
        String order = "";
        List<DEMRegistration> expResult = list;
        List<DEMRegistration> result = instance.search(filter, start, count, orderField, order);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSearchCount() {
        System.out.println("searchCount");
        SearchFilter filter = new SearchFilter();
        int expResult = list.size();
        int result = instance.searchCount(filter);
        Assert.assertEquals(expResult, result);
    }

    public static String nextString(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
